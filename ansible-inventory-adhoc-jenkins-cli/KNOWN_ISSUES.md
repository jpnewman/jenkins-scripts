
# ERRORS

## TCP port for JNLP agents not fixed

### ERROR

~~~
java.io.IOException: No X-Jenkins-CLI2-Port among [X-Jenkins, null, Server, X-Content-Type-Options, Connection, X-You-Are-In-Group, X-Hudson, X-Permission-Implied-By, Date, X-Jenkins-Session, X-You-Are-Authenticated-As, X-Required-Permission, Set-Cookie, Expires, Content-Length, Content-Type]
	at hudson.cli.CLI.getCliTcpPort(CLI.java:284)
	at hudson.cli.CLI.<init>(CLI.java:128)
	at hudson.cli.CLIConnectionFactory.connect(CLIConnectionFactory.java:72)
	at hudson.cli.CLI._main(CLI.java:473)
	at hudson.cli.CLI.main(CLI.java:384)
	Suppressed: java.io.EOFException: unexpected stream termination
		at hudson.remoting.ChannelBuilder.negotiate(ChannelBuilder.java:365)
		at hudson.remoting.Channel.<init>(Channel.java:460)
		at hudson.remoting.Channel.<init>(Channel.java:438)
		at hudson.remoting.Channel.<init>(Channel.java:434)
		at hudson.remoting.Channel.<init>(Channel.java:422)
		at hudson.remoting.Channel.<init>(Channel.java:413)
		at hudson.remoting.Channel.<init>(Channel.java:386)
		at hudson.cli.CLI.connectViaHttp(CLI.java:153)
		at hudson.cli.CLI.<init>(CLI.java:132)
		... 3 more
~~~

### FIX

<https://github.com/sebastianbergmann/php-jenkins-template/issues/64>

Set JNLP fixed port.

1. Login as a admin user. e.g. ```jenkins-admin```
2. Navigate to 'Manage Jenkins' -> 'Configure Global Security'
3. Click 'Enable security'
4. Select 'TCP port for JNLP agents' to 'Fixed', with value '49187'
5. Click 'Save'
