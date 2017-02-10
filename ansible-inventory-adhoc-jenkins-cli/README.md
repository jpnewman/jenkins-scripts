
# Jenkins Info

This Python script uses an Ansible inventory file to execute Jenkins-cli commands on jenkins masters.

The script downloads the ```jenkins-cli.jar``` from each master and executes the requested jenkins-cli command.

## References

- <https://wiki.jenkins-ci.org/display/JENKINS/Jenkins+CLI>

## Setting up user SSH key

If needed, generate SSH keys using the following command: -

~~~
ssh-keygen -t rsa -N "" -f jenkins-admin
~~~

To access Jenkins via SSH a users public SSH key needs to be set: -

_e.g._

<http://JENKINS-SERVER/user/USER/configure>

1. Login as a admin user. e.g. ```jenkins-admin```
2. Navigate to their configuration e.g. ```http://JENKINS-SERVER/user/jenkins-admin/configure```
3. Ensure that their ```SSH Public Keys``` is set correctly.

## Set JNLP fixed port

1. Login as a admin user. e.g. ```jenkins-admin```
2. Navigate to 'Manage Jenkins' -> 'Configure Global Security'
3. Click 'Enable security'
4. Select 'TCP port for JNLP agents' to 'Fixed', with value '49187'
5. Allow the required 'Agent protocols...'
5. Click 'Save'

> The 'TCP port for JNLP agents' (e.g. '49187') should be opened to and on the Jenkins master.

## Check SSH Endpoint port

<https://jenkins.io/doc/book/managing/cli/>

~~~
curl -Lvk https://jenkins-server/login 2>&1 | grep 'X-SSH-Endpoint'
< X-SSH-Endpoint: jenkins-server:46268
~~~

#### Install dependencies

> Mac OS X

~~~
brew tap homebrew/dupes/expect
brew install homebrew/dupes/expect
~~~

## Run

~~~
./jenkins.py ansible_hosts

./jenkins.py ansible_hosts version --pattern jenkins-server
~~~

## Run, SSH key

~~~
./jenkins.py ansible_hosts version --pattern localhost --debug -i jenkins-admin
~~~

> With ```--debug``` passwords will be displayed in stdout

## Run, groovy script

~~~
./jenkins.py ansible_hosts 'groovy ./groovy/get-node-details.groovy' --pattern localhost --debug -i jenkins-admin
~~~

#### Run, groovy script (get-node-details.groovy)

~~~
unbuffer ./jenkins.py ../ansible_hosts 'groovy ../spec/groovy/get-node-details.groovy' -i ~/.ssh/builduser --pattern gs-jenkins | tee jenkins.log
~~~

#### Run, groovy script (get-jobs-details.groovy)

~~~
unbuffer ./jenkins.py ../ansible_hosts 'groovy ../spec/groovy/get-jobs-details.groovy' -i ~/.ssh/builduser --pattern gs-jenkins | tee jenkins-jobs.log
~~~

#### Run, groovy script (jenkins-ci-report.groovy)

~~~
unbuffer ./jenkins.py ../ansible_hosts 'groovy ../spec/groovy/jenkins-ci-report.groovy' -i ~/.ssh/builduser --pattern aws-jenkins,gs-jenkins --no-headers --skip-errors --prefix-file jenkins-ci-report_header.txt | tee jenkins-ci-report.md
~~~

#### Run, without headers

~~~
unbuffer ./jenkins.py ../ansible_hosts 'groovy ../spec/groovy/get-jobs-details.groovy' -i ~/.ssh/builduser --pattern aws-jenkins,gs-jenkins --no-headers | tee jenkins-jobs-no-headers.log
~~~

#### Run, skip errors

~~~
unbuffer ./jenkins.py ../ansible_hosts 'groovy ../spec/groovy/get-jobs-details.groovy' -i ~/.ssh/builduser --pattern aws-jenkins,gs-jenkins --no-headers --skip-errors | tee jenkins-jobs-no-headers-skip-errors.log
~~~


## Run, log

~~~
unbuffer ./jenkins.py ansible_hosts --pattern jenkins-server | tee jenkins.log
~~~

## Run, debug

~~~
./jenkins.py ansible_hosts --pattern jenkins-server --debug
~~~

> With ```--debug``` passwords will be displayed in stdout

## Debug, pdb

~~~
python -m pdb jenkins.py ansible_hosts --pattern jenkins-server
~~~

## Debugging, pudb

~~~
pip install pudb

pudb jenkins.py ansible_hosts --pattern jenkins-server
~~~
