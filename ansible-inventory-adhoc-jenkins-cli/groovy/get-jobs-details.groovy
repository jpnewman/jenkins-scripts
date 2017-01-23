import hudson.model.*
import jenkins.model.Jenkins

lastestSuccessful = null
lastestFailed = null
maxBuildDate = null

// println("Total number of Jobs: ${Hudson.instance.items.size()}")

for (item in Hudson.instance.items) {
  // println(item.name)

  def lastSuccessfulBuild = item.getLastSuccessfulBuild()
  if (lastSuccessfulBuild) {
    def lastSuccessfulBuildDate = lastSuccessfulBuild.getTime()
    // println("  Last Successful: ${lastSuccessfulBuildDate.format("YYYY-MMM-DD HH:MM:SS")}")

    if (lastSuccessfulBuildDate > lastestSuccessful) {
      lastestSuccessful = lastSuccessfulBuildDate
    }
  }

  def lastFailedBuild = item.getLastFailedBuild()
  if (lastFailedBuild) {
    lastFailedBuildDate = lastFailedBuild.getTime()
    // println("  Last Failed: ${lastFailedBuildDate.format("YYYY-MMM-DD HH:MM:SS")}")

    if (lastFailedBuildDate > lastestFailed) {
      lastestFailed = lastFailedBuildDate
    }
  }
}

/*
if (lastestSuccessful) {
  println("Lastest Successful: ${lastestSuccessful}")
}

if (lastestFailed) {
  println("Lastest Failed: ${lastestFailed}")
}
*/

if (lastestSuccessful && lastestFailed) {
  def dates = [lastestSuccessful, lastestFailed]
  dates.sort()
  maxBuildDate = dates[dates.size() - 1]
  // println("Lastest Job Date: ${maxBuildDate.format("YYYY-MMM-DD HH:MM:SS")}")
}

println("${InetAddress.localHost.canonicalHostName} ${Hudson.instance.items.size()} ${maxBuildDate.format("YYYY-MM-DD")}")
