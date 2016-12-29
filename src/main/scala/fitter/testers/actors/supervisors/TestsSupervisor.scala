package fitter.testers.actors.supervisors

import akka.actor.{Actor, Props}
import fitter.testers.actors.DeployGenerator
import fitter.testers.commands.Start
import fitter.testers.config.{SessionConfig, SystemConfig}

abstract class TestsSupervisor(systemConfig: SystemConfig, sessionConfig: SessionConfig) extends Actor {

  override def preStart(): Unit = {
    systemConfig.nodesAddresses.foreach(address => {
      val deploy = DeployGenerator.makeRemoteDeploy(systemConfig.actorSystemName, address, systemConfig.nodesPort)
      context.actorOf(Props(classOf[BootstrappingAgent], sessionConfig).withDeploy(deploy))
    })
  }

  override def receive: Receive = {
    case Start => run()
  }

  protected def run(): Unit
}
