import jenkins.*
import jenkins.model.*
import hudson.*
import hudson.model.*

import java.net.*
import java.util.*

import hudson.model.*
import hudson.util.RemotingDiagnostics

enum JenkinsType {
  MASTER,
  SLAVE
}

abstract class BaseDetail {
  public JenkinsType type
  public String hostname
  public String notes
  public ArrayList<String> ipAddresses = []
  public String usage
  public String networkLocation
}

class SlaveDetail extends BaseDetail {
  public ArrayList<String> slaveLabals = []
}

class MasterDetail extends BaseDetail {
  public String team
  public String owner
  public String deplomentTools
  public Integer totalJobs
  public lastSuccessfulBuild
  public lastFailedBuild
  public Date lastBuild
  public String recoverySystem
  public String decomDate
  public ArrayList<SlaveDetail> slaves = []
}

def getInetAddresses(detail, hostname) {
  try {
    ArrayList hostAddresses = InetAddress.getAllByName(hostname).hostAddress
    for (hostAddress in hostAddresses) {
      detail.ipAddresses.add(hostAddress)
    }
  } catch (java.net.UnknownHostException ex) {
    detail.ipAddresses = []
  }
}

def getRemoteInetAddress(detail, slave) {
  script = "println(InetAddress.localHost.hostAddress)"
  try {
    String results = RemotingDiagnostics.executeGroovy(script, slave.getChannel())
    ArrayList hostAddresses = results.replaceAll('\\[', '').replaceAll('\\]', '').trim().split(',')
    for (hostAddress in hostAddresses) {
      detail.ipAddresses.add(hostAddress)
    }
  } catch (all) {
    // all.printStackTrace();
    detail.ipAddresses = []
  }
}

def getInterfaceInetAddresses(detail) {
  Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces()
  while (networkInterfaces.hasMoreElements()) {
    NetworkInterface networkInterface = networkInterfaces.nextElement()
    Enumeration<InetAddress> iNetAddresses = networkInterface.getInetAddresses()
    while (iNetAddresses.hasMoreElements()) {
      InetAddress iNetAddress = iNetAddresses.nextElement()
      if (iNetAddress instanceof Inet4Address) {
        String hostAddress = iNetAddress.getHostAddress()
        if (hostAddress != '127.0.0.1') {
          detail.ipAddresses.add(hostAddress)
        }
      }
    }
  }
}

def getMasterJobDetail(detail) {
  detail.totalJobs = Hudson.instance.items.size()

  for (item in Hudson.instance.items) {
    lastSuccessfulBuild = item.getLastSuccessfulBuild()
    if (lastSuccessfulBuild) {
      detail.lastSuccessfulBuild = [detail.lastSuccessfulBuild, lastSuccessfulBuild.getTime()].sort()[1]
    }

    lastFailedBuild = item.getLastFailedBuild()
    if (lastFailedBuild) {
      detail.lastFailedBuild = [detail.lastFailedBuild, lastFailedBuild.getTime()].sort()[1]
    }

    detail.lastBuild = [detail.lastSuccessfulBuild, detail.lastFailedBuild].sort()[1]
  }
}

def getSlaveLabels(detail, slave) {
  slaveLabel = slave.getLabelString()
  if (slaveLabel) {
    detail.slaveLabals = slaveLabel.split(' ')
  }
}

def getMasterDetails(details) {
  masterDetail = new MasterDetail()
  masterDetail.type = JenkinsType.MASTER
  masterDetail.hostname = InetAddress.localHost.canonicalHostName

  getInterfaceInetAddresses(masterDetail)
  getMasterJobDetail(masterDetail)

  masterDetail.slaves = getSlaveDetails()

  details[masterDetail.hostname] = masterDetail
}

def getSlaveDetails() {
  slaves = []
  for (slave in Jenkins.instance.slaves) {
    slaveDetail = new SlaveDetail()
    slaveDetail.type = JenkinsType.SLAVE
    slaveDetail.hostname = slave.name

    getRemoteInetAddress(slaveDetail, slave)
    getSlaveLabels(slaveDetail, slave)

    slaves.add(slaveDetail)
  }

  return slaves
}

def printDetailMarkdownTableHeader() {
  headers = ['Hostname',
             'Notes',
             'ipAddresses',
             'Usage&nbsp;%',
             'Region&nbsp;/&nbsp;Teams',
             'Owner',
             'Deploment&nbsp;Tools',
             'Network&nbsp;Location',
             'Number&nbsp;of&nbsp;Jobs',
             'Latest&nbsp;Build&nbsp;Date',
             'Agent&nbsp;Labels&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;',
             'Decommission Target Date'
            ]
  println("|${headers.join('|')}|")
  headers.size().times {
    print("|---")
  }
  println('|')
}

def printDetailMarkdownTable(details) {
  // printDetailMarkdownTableHeader()

  details.each { masterDetail ->
    columns = [sprintf('**%s**', masterDetail.value.hostname),
              '',
              sprintf('**%s**', masterDetail.value.ipAddresses.join('<br />')),
              '',
              '',
              '',
              '',
              '',
              sprintf('**%s**', masterDetail.value.totalJobs ?: ''),
              sprintf('**%s**', masterDetail.value.lastBuild ? masterDetail.value.lastBuild.format("YYYY-MM-DD") : ''),
              '',
              '']
    println("|${columns.join('|')}|")

    for (slaveDetail in masterDetail.value.slaves) {
      columns = [slaveDetail.hostname,
                '',
                slaveDetail.ipAddresses.join('<br />'),
                '',
                '',
                '',
                '',
                '',
                '',
                '',
                sprintf("%s", slaveDetail.slaveLabals.collect { "```$it```" }.join('<br />')),
                '']
      println("|${columns.join('|')}|")
    }
  }
}

def main() {
  details = [:]
  getMasterDetails(details)
  printDetailMarkdownTable(details)
}

main()
