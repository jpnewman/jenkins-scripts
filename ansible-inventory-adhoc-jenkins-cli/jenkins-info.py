#!/usr/bin/env python

# http://docs.ansible.com/ansible/dev_guide/developing_api.html#python-api-2-0
# http://stackoverflow.com/questions/37623849/how-can-i-get-a-list-of-hosts-from-an-ansible-inventory-file

import os
import re
import argparse
import subprocess

from ansible.parsing.dataloader import DataLoader
from ansible.vars import VariableManager
from ansible.inventory import Inventory
from ansible.errors import AnsibleError


KEYSTORE_FILE = 'jenkins.key'
KEYSTORE_PASSWORD = 'password123'

JENKINS_CLI_PATH = '/tmp/jenkins-cli.jar'

DEFAULT_UNWANTED_SSH_LINES = [
    re.escape('Skipping HTTPS certificate checks altogether. Note that this is not secure at all.')
]

DEBUG = False


class Command():
    def __init__(self, command):
        debug(command)

        self.command = command

        p = subprocess.Popen(
            command, shell=True,
            stdin=subprocess.PIPE,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
        )

        self.stdout, self.stderr = p.communicate()
        self.rc = p.returncode

    @property
    def rc(self):
        return self.rc

    @property
    def stdout(self):
        return self.stdout if self.stdout else ''

    @property
    def stderr(self):
        return self.stderr if self.stderr else ''


def debug(msg):
    if DEBUG:
        print(msg)


def _parse_args():
    """Parse Command Arguments."""
    global DEBUG

    desc = 'Jenkins Info, via Ansible'
    parser = argparse.ArgumentParser(description=desc)

    parser.add_argument('inventory_file',
                        help='Inventory File')
    parser.add_argument('command',
                        help='Jenkins-cli command',
                        nargs='?',
                        default='version')
    parser.add_argument('--pattern',
                        help='Pattern',
                        nargs='?',
                        default='')
    parser.add_argument('-i', '--identity_file',
                        help='identity_file',
                        nargs='?',
                        default='')
    parser.add_argument('--debug',
                        action='store_true',
                        default=False,
                        help='Debug output')

    args = parser.parse_args()
    DEBUG = args.debug

    return args


def get_ansible_hosts(inventory_file, pattern=''):
    #  Ansible: initialize needed objects
    variable_manager = VariableManager()
    loader = DataLoader()

    #  Ansible: Load inventory
    inventory = Inventory(
        loader=loader,
        variable_manager=variable_manager,
        host_list=inventory_file
    )

    hosts = inventory.list_hosts(pattern)

    if len(hosts) == 0:
        raise AnsibleError("Specified hosts options do not match any hosts")

    return hosts


def display_hosts(hosts):
    print("Hosts ({0}):".format(len(hosts)))
    for host in hosts:
        print("  {0}".format(host))


def get_http_protocol(host):
    # Test if reverse proxy is forcing HTTPS
    cmd = "curl -I http://{0}".format(host)

    html_header = Command(cmd).stdout
    if '301 Moved Permanently' in html_header \
       and 'Location: https://' in html_header:
        return "https"

    return "http"


def create_keystore(host_name, port=443):
    host_cer_file = "{0}.cer".format(host_name)

    # Get current key
    Command("openssl s_client -connect {0}:{1} </dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > {2}".format(host_name, port, host_cer_file))

    # Delete existing key
    if os.path.isfile(KEYSTORE_FILE):
        stored_certs = Command("keytool -list -alias {0} -keystore {1} -storepass {2}".format(host_name, KEYSTORE_FILE, KEYSTORE_PASSWORD))
        # assert stored_certs.rc == 0

        if host_name in stored_certs.stdout.strip():
            Command("keytool -delete -alias {0} -keystore {1} -storepass {2}".format(host_name, KEYSTORE_FILE, KEYSTORE_PASSWORD))

    # Import key
    Command("keytool -import -noprompt -trustcacerts -alias {0} -file {1} -keystore {2} -storepass {3}".format(host_name, host_cer_file, KEYSTORE_FILE, KEYSTORE_PASSWORD))

    # Remove cer file
    # Command("rm {0}".format(host_cer_file))


def get_jenkins_cli(host_name, port=80):
    """Get jenkins-cli.jar from server."""

    url = "http://{0}:{1}/jnlpJars/jenkins-cli.jar".format(host_name, port)

    wget_options = ["--output-document={0}".format(JENKINS_CLI_PATH),
                    '--no-check-certificate']

    jenkins_cli = Command("wget {0} {1}".format(url, ' '.join(wget_options)))
    if jenkins_cli.rc != 0:
        raise Exception("ERROR: wget - {0}".format(jenkins_cli.stderr))


def remove_unwanted_lines(lines, unwanted_lines_regexes):
    combined = re.compile("(" + ")|(".join(unwanted_lines_regexes) + ")")

    lines = filter(lambda line: not re.match(combined, line), lines)

    return lines


def run_jenkins_cli_command(jenkins_cli_path,
                            host_name,
                            jenkins_cli_command,
                            java_options=[],
                            jar_options=[]):
    """Runs Jenkins CLI command and return a string."""

    # result = Command("echo {0}".format(host))
    # print(result)

    http_protocol = get_http_protocol(host_name)
    # print(http_protocol)

    if http_protocol == 'https':
        create_keystore(host_name)
        java_options += ["-Djavax.net.ssl.trustStore={0}".format(KEYSTORE_FILE)]
        java_options += ["-Djavax.net.ssl.trustStorePassword={0}".format(KEYSTORE_PASSWORD)]

    jar_options += ['-noCertificateCheck']
    jar_options += ["-s {0}://{1}".format(http_protocol, host_name)]

    cmd = "java {0} -jar {1} {2} {3}".format(' '.join(java_options),
                                             jenkins_cli_path,
                                             ' '.join(jar_options),
                                             jenkins_cli_command)

    response = Command(cmd)

    response_lines = [s for s in response.stdout.splitlines()]
    response_lines = remove_unwanted_lines(response_lines, DEFAULT_UNWANTED_SSH_LINES)

    debug(response.rc)
    debug(response.stdout)
    debug(response.stderr)

    return "\n".join(response_lines)


def process_host(host_name, ssh_identity_file, jenkins_cli_command):

    get_jenkins_cli(host_name)

    jar_options = []

    if ssh_identity_file:
        if not os.path.isabs(ssh_identity_file):
            ssh_identity_file = os.path.expanduser(ssh_identity_file)

        jar_options += ["-i {0}".format(ssh_identity_file)]

    response = run_jenkins_cli_command(JENKINS_CLI_PATH,
                                       host_name,
                                       jenkins_cli_command,
                                       jar_options=jar_options)

    print(response)


def process_hosts(hosts, jenkins_cli_command, ssh_identity_file):
    for host in hosts:
        host_name = host.get_name()
        print('=' * 80)
        print(host_name)
        print('-' * 80)

        process_host(host_name, ssh_identity_file, jenkins_cli_command)


def main():
    """Main function."""
    args = _parse_args()

    hosts = get_ansible_hosts(args.inventory_file, args.pattern)
    display_hosts(hosts)
    process_hosts(hosts, args.command, args.identity_file)


if __name__ == "__main__":
    main()
