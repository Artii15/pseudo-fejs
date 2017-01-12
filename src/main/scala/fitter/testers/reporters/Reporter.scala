package fitter.testers.reporters

import fitter.testers.results.TaskReport
import fitter.testers.results.events.ParticipantsReport
import fitter.testers.results.posts.{PostCommentsReport, PostLikesReport}
import fitter.testers.results.registration.RegistrationReport

object Reporter {
  def showReport(taskReport: TaskReport): Unit = taskReport match {
    case registrationReport: RegistrationReport => showRegistrationReport(registrationReport)
    case participantsReport: ParticipantsReport => showParticipantsReport(participantsReport)
    case postLikesReport: PostLikesReport => showPostLikesReport(postLikesReport)
    case postCommentsReport: PostCommentsReport => showPostCommentsReport(postCommentsReport)
  }

  private def showRegistrationReport(registrationReport: RegistrationReport): Unit = {
    println(s"Created accounts:")
    registrationReport.accounts.foreach(account => println(s"${account.nick}\t${account.password}"))
    println(s"Number of created accounts: ${registrationReport.accounts.size}")
  }

  private def showParticipantsReport(participantsReport: ParticipantsReport): Unit = {
    println(s"Event participants: ")
    participantsReport.participants.foreach(participant => println(s"${participant.nick}\t${participant.password}"))
    println(s"Number of participants: ${participantsReport.participants.size}")
  }

  private def showPostLikesReport(postLikesReport: PostLikesReport): Unit = {
    println(s"Post likers: ")
    postLikesReport.accounts.distinct.foreach(account => println(s"${account}"))
    println(s"Number of likes: ${postLikesReport.accounts.distinct.size}")
  }

  private def showPostCommentsReport(postCommentsReport: PostCommentsReport): Unit = {
    println(s"Number of comments: ${postCommentsReport.accounts.size}")
  }
}
