object Main {
  def main(args: Array[String]): Unit = {
    ClusterConnector.doInCluster("127.0.0.1")(cluster => {
      SessionConnector.makeConnector(cluster)("test")(session => {

      })
    })
  }
}
