
# Debug Ansible

## Debugging, pdb

~~~
python -m pdb `where ansible` -i ansible_hosts localhost -m ping
~~~

~~~
(Pdb) b 91
Breakpoint 1 at /usr/local/bin/ansible:91
(Pdb) b 92
Breakpoint 1 at /usr/local/bin/ansible:92
(Pdb) c
> /usr/local/bin/ansible(91)<module>()
-> cli.parse()
(Pdb) s
--Call--
> /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py(49)parse()
-> def parse(self):
(Pdb) b 81
Breakpoint 2 at /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py:81
(Pdb) c
> /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py(81)parse()
-> return True
(Pdb) p self.options.inventory
'ansible_hosts'
(Pdb) c
> /usr/local/bin/ansible(92)<module>()
-> exit_code = cli.run()
(Pdb) s
--Call--
> /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py(91)run()
-> def run(self):
(Pdb) b 124
Breakpoint 4 at /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py:124
(Pdb) c
> /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py(124)run()
-> inventory = Inventory(loader=loader, variable_manager=variable_manager, host_list=self.options.inventory)
(Pdb) p pattern
'localhost'
(Pdb)
~~~


~~~
(Pdb) b 44
Breakpoint 1 at /usr/local/bin/ansible:44
(Pdb) b 90
Breakpoint 1 at /usr/local/bin/ansible:90
(Pdb) c
> /usr/local/bin/ansible(44)<module>()
-> import ansible.constants as C
(Pdb) n
(Pdb) dir(C)
['ACCELERATE_CONNECT_TIMEOUT', 'ACCELERATE_DAEMON_TIMEOUT', 'ACCELERATE_KEYS_DIR', 'ACCELERATE_KEYS_DIR_PERMS', 'ACCELERATE_KEYS_FILE_PERMS', 'ACCELERATE_MULTI_KEY', 'ACCELERATE_PORT', 'ACCELERATE_TIMEOUT', 'ANSIBLE_COW_SELECTION', 'ANSIBLE_COW_WHITELIST', 'ANSIBLE_FORCE_COLOR', 'ANSIBLE_NOCOLOR', 'ANSIBLE_NOCOWS', 'ANSIBLE_SSH_ARGS', 'ANSIBLE_SSH_CONTROL_PATH', 'ANSIBLE_SSH_PIPELINING', 'ANSIBLE_SSH_RETRIES', 'AnsibleOptionsError', 'BECOME_ALLOW_SAME_USER', 'BECOME_ERROR_STRINGS', 'BECOME_METHODS', 'BECOME_MISSING_STRINGS', 'CACHE_PLUGIN', 'CACHE_PLUGIN_CONNECTION', 'CACHE_PLUGIN_PREFIX', 'CACHE_PLUGIN_TIMEOUT', 'COMMAND_WARNINGS', 'CONFIG_FILE', 'DEFAULTS', 'DEFAULT_ACTION_PLUGIN_PATH', 'DEFAULT_ASK_PASS', 'DEFAULT_ASK_SUDO_PASS', 'DEFAULT_ASK_SU_PASS', 'DEFAULT_ASK_VAULT_PASS', 'DEFAULT_BECOME', 'DEFAULT_BECOME_ASK_PASS', 'DEFAULT_BECOME_EXE', 'DEFAULT_BECOME_FLAGS', 'DEFAULT_BECOME_METHOD', 'DEFAULT_BECOME_PASS', 'DEFAULT_BECOME_USER', 'DEFAULT_CACHE_PLUGIN_PATH', 'DEFAULT_CALLABLE_WHITELIST', 'DEFAULT_CALLBACK_PLUGIN_PATH', 'DEFAULT_CALLBACK_WHITELIST', 'DEFAULT_CONNECTION_PLUGIN_PATH', 'DEFAULT_COW_WHITELIST', 'DEFAULT_DEBUG', 'DEFAULT_EXECUTABLE', 'DEFAULT_FILTER_PLUGIN_PATH', 'DEFAULT_FORCE_HANDLERS', 'DEFAULT_FORKS', 'DEFAULT_GATHERING', 'DEFAULT_HASH_BEHAVIOUR', 'DEFAULT_HOST_LIST', 'DEFAULT_INVENTORY_IGNORE', 'DEFAULT_INVENTORY_PLUGIN_PATH', 'DEFAULT_JINJA2_EXTENSIONS', 'DEFAULT_KEEP_REMOTE_FILES', 'DEFAULT_LOAD_CALLBACK_PLUGINS', 'DEFAULT_LOG_PATH', 'DEFAULT_LOOKUP_PLUGIN_PATH', 'DEFAULT_MANAGED_STR', 'DEFAULT_MODULE_ARGS', 'DEFAULT_MODULE_LANG', 'DEFAULT_MODULE_NAME', 'DEFAULT_MODULE_PATH', 'DEFAULT_NO_LOG', 'DEFAULT_NO_TARGET_SYSLOG', 'DEFAULT_NULL_REPRESENTATION', 'DEFAULT_PASSWORD_CHARS', 'DEFAULT_PATTERN', 'DEFAULT_POLL_INTERVAL', 'DEFAULT_PRIVATE_KEY_FILE', 'DEFAULT_PRIVATE_ROLE_VARS', 'DEFAULT_REMOTE_PASS', 'DEFAULT_REMOTE_PORT', 'DEFAULT_REMOTE_TMP', 'DEFAULT_REMOTE_USER', 'DEFAULT_ROLES_PATH', 'DEFAULT_SCP_IF_SSH', 'DEFAULT_SELINUX_SPECIAL_FS', 'DEFAULT_SFTP_BATCH_MODE', 'DEFAULT_SQUASH_ACTIONS', 'DEFAULT_STDOUT_CALLBACK', 'DEFAULT_SU', 'DEFAULT_SUBSET', 'DEFAULT_SUDO', 'DEFAULT_SUDO_EXE', 'DEFAULT_SUDO_FLAGS', 'DEFAULT_SUDO_PASS', 'DEFAULT_SUDO_USER', 'DEFAULT_SU_EXE', 'DEFAULT_SU_FLAGS', 'DEFAULT_SU_PASS', 'DEFAULT_SU_USER', 'DEFAULT_SYSLOG_FACILITY', 'DEFAULT_TEST_PLUGIN_PATH', 'DEFAULT_TIMEOUT', 'DEFAULT_TRANSPORT', 'DEFAULT_UNDEFINED_VAR_BEHAVIOR', 'DEFAULT_VARS_PLUGIN_PATH', 'DEFAULT_VAR_COMPRESSION_LEVEL', 'DEFAULT_VAULT_PASSWORD_FILE', 'DEPRECATED_HOST_LIST', 'DEPRECATION_WARNINGS', 'DISPLAY_SKIPPED_HOSTS', 'GALAXY_IGNORE_CERTS', 'GALAXY_SCMS', 'GALAXY_SERVER', 'HOST_KEY_CHECKING', 'LOCALHOST', 'MAX_FILE_SIZE_FOR_DIFF', 'MODULE_NO_JSON', 'MODULE_REQUIRE_ARGS', 'PARAMIKO_PTY', 'PARAMIKO_RECORD_HOST_KEYS', 'RETRY_FILES_ENABLED', 'RETRY_FILES_SAVE_PATH', 'STRING_TYPE_FILTERS', 'SYSTEM_WARNINGS', 'TREE_DIR', 'VAULT_VERSION_MAX', 'VAULT_VERSION_MIN', 'YAML_FILENAME_EXTENSIONS', 'ZEROMQ_PORT', '__builtins__', '__doc__', '__file__', '__metaclass__', '__name__', '__package__', '_get_config', 'absolute_import', 'ascii_letters', 'configparser', 'digits', 'division', 'get_config', 'load_config_file', 'mk_boolean', 'os', 'p', 'print_function', 'shell_expand', 'string_types', 'unquote']
(Pdb) p C.DEFAULT_HOST_LIST
/etc/ansible/hosts
(Pdb) p C.LOCALHOST
frozenset(['::1', 'localhost', '127.0.0.1'])
(Pdb) c
> /usr/local/bin/ansible(90)<module>()
(Pdb) n
-> cli = mycli(sys.argv)
(Pdb) dir(cli)
['LESS_OPTS', 'PAGER', 'VALID_ACTIONS', '_BOLD', '_CONST', '_ITALIC', '_MODULE', '_URL', '__class__', '__delattr__', '__dict__', '__doc__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_git_repo_info', '_gitinfo', '_play_ds', '_terminate', 'action', 'args', 'ask_passwords', 'ask_vault_passwords', 'base_parser', 'callback', 'execute', 'expand_tilde', 'get_opt', 'normalize_become_options', 'options', 'pager', 'pager_pipe', 'parse', 'parser', 'read_vault_password_file', 'run', 'set_action', 'tty_ify', 'validate_conflicts', 'version', 'version_info']
> /usr/local/bin/ansible(91)<module>()
(Pdb) n
-> cli.parse()
(Pdb) s
(Pdb) b 81
Breakpoint 2 at /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py:81
(Pdb) c
> /usr/local/lib/python2.7/site-packages/ansible/cli/adhoc.py(81)parse()
-> return True
(Pdb) dir(self)
['LESS_OPTS', 'PAGER', 'VALID_ACTIONS', '_BOLD', '_CONST', '_ITALIC', '_MODULE', '_URL', '__class__', '__delattr__', '__dict__', '__doc__', '__format__', '__getattribute__', '__hash__', '__init__', '__module__', '__new__', '__reduce__', '__reduce_ex__', '__repr__', '__setattr__', '__sizeof__', '__str__', '__subclasshook__', '__weakref__', '_git_repo_info', '_gitinfo', '_play_ds', '_terminate', 'action', 'args', 'ask_passwords', 'ask_vault_passwords', 'base_parser', 'callback', 'execute', 'expand_tilde', 'get_opt', 'normalize_become_options', 'options', 'pager', 'pager_pipe', 'parse', 'parser', 'read_vault_password_file', 'run', 'set_action', 'tty_ify', 'validate_conflicts', 'version', 'version_info']
(Pdb) dir(self.parser)
['__doc__', '__init__', '__module__', '_add_help_option', '_add_version_option', '_check_conflict', '_create_option_list', '_create_option_mappings', '_get_all_options', '_get_args', '_get_encoding', '_init_parsing_state', '_long_opt', '_match_long_opt', '_populate_option_list', '_process_args', '_process_long_opt', '_process_short_opts', '_share_option_mappings', '_short_opt', 'add_option', 'add_option_group', 'add_options', 'allow_interspersed_args', 'check_values', 'conflict_handler', 'defaults', 'description', 'destroy', 'disable_interspersed_args', 'enable_interspersed_args', 'epilog', 'error', 'exit', 'expand_prog_name', 'format_description', 'format_epilog', 'format_help', 'format_option_help', 'formatter', 'get_default_values', 'get_description', 'get_option', 'get_option_group', 'get_prog_name', 'get_usage', 'get_version', 'has_option', 'largs', 'option_class', 'option_groups', 'option_list', 'parse_args', 'print_help', 'print_usage', 'print_version', 'process_default_values', 'prog', 'rargs', 'remove_option', 'set_conflict_handler', 'set_default', 'set_defaults', 'set_description', 'set_process_default_values', 'set_usage', 'standard_option_list', 'usage', 'values', 'version']
(Pdb) p self.options
<Values at 0x1103608c0: {'subset': None, 'ask_pass': False, 'become_user': None, 'poll_interval': 15, 'sudo': False, 'private_key_file': None, 'syntax': None, 'one_line': None, 'diff': False, 'sftp_extra_args': '', 'check': False, 'remote_user': None, 'become_method': 'sudo', 'vault_password_file': None, 'output_file': None, 'ask_su_pass': False, 'new_vault_password_file': None, 'inventory': 'ansible_hosts', 'forks': 5, 'listhosts': None, 'ssh_extra_args': '', 'module_name': 'ping', 'become_ask_pass': False, 'seconds': 0, 'module_path': None, 'su_user': None, 'ask_sudo_pass': False, 'extra_vars': [], 'verbosity': 0, 'tree': None, 'su': False, 'scp_extra_args': '', 'connection': 'smart', 'ask_vault_pass': False, 'timeout': 10, 'become': False, 'sudo_user': None, 'ssh_common_args': '', 'module_args': ''}>
(Pdb) p self.options.inventory
'ansible_hosts'
~~~

## Debugging, pudb

~~~
pip install pudb

pudb `where ansible` -i ansible_hosts localhost -m ping
~~~
