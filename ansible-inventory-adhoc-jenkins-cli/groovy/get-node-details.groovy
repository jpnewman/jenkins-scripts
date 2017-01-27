import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

import java.net.*

server_host = InetAddress.localHost.canonicalHostName
server_addr = InetAddress.getAllByName(server_host)
println "${server_host} ${server_addr}"
for (slave in Jenkins.instance.slaves) {
  try {
    host_addr = " ${InetAddress.getAllByName(slave.name).hostAddress}"
  } catch (java.net.UnknownHostException ex) {
    host_addr = ''
  }

  slave_label = slave.getLabelString()
  if (slave_label) {
    slave_labels = slave_label.split(' ').join('\n')
    slave_label = "\n${slave_labels}"
  }

  println "${slave.name}${host_addr} (${slave.computer.offline})${slave_label}"
}
