import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

import java.net.*

server_host = InetAddress.localHost.canonicalHostName
server_addr =InetAddress.getAllByName(server_host)
println "${server_host} ${server_addr}"
for (slave in Jenkins.instance.slaves) {
  try {
    addr = InetAddress.getAllByName(slave.name)
  } catch (java.net.UnknownHostException ex) {
    addr = ''
  }
  if (addr) {
	  println "  ${slave.name} ${addr.hostAddress} (${slave.computer.offline})"
  } else {
	  println "  ${slave.name} (${slave.computer.offline})"
  }
}
