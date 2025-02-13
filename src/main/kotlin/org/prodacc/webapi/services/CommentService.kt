package org.prodacc.webapi.services

import jakarta.transaction.Transactional
import org.prodacc.webapi.models.JobCardComments
import org.prodacc.webapi.repositories.CommentsRepository
import org.prodacc.webapi.repositories.EmployeeRepository
import org.prodacc.webapi.repositories.JobCardRepository
import org.prodacc.webapi.services.dataTransferObjects.Comment
import org.prodacc.webapi.services.dataTransferObjects.NewComment
import org.prodacc.webapi.services.synchronisation.WebSocketHandler
import org.springframework.stereotype.Service
import java.util.*

@Service
class CommentService(
    private val commentRepository: CommentsRepository,
    private val webSocketHandler: WebSocketHandler,
    private val jobCardRepository: JobCardRepository,
    private val employeeRepository: EmployeeRepository
) {
    fun getAllJobCardComments(jobCardId: String): List<Comment> {
        return try {
            val jobCard =
                jobCardRepository.findById(UUID.fromString(jobCardId)).orElseThrow { Exception("Job Card not found.") }
            commentRepository.getCommentsByJobCardId(jobCard).map { it.toComment() }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getCommentById(commentId: String): Comment {
        return try {
            commentRepository.findById(UUID.fromString(commentId)).map { it.toComment() }
                .orElseThrow { Exception("Comment not found.") }
        } catch (e: Exception) {
            throw e
        }
    }

    fun getAllComments(): List<Comment> {
        return commentRepository.findAll().map { it.toComment() }
    }

    fun createComment(comment: NewComment): Comment {
        return try {
            val newComment = commentRepository.save(comment.toJobCardComments())
            webSocketHandler.broadcastUpdate("NEW_COMMENT", comment.jobCardId)
            newComment.toComment()
        } catch (e: Exception) {
            throw e
        }

    }

    @Transactional
    fun updateComment(comment: Comment, commentId: String): Comment {
        return try {
            val oldComment =
                commentRepository.findById(UUID.fromString(commentId)).orElseThrow { Exception("Comment not found.") }
            val updatedComment = commentRepository.save(
                JobCardComments(
                    jobCardCommentId = oldComment.jobCardCommentId,
                    jobCardId = oldComment.jobCardId,
                    employee = oldComment.employee,
                    comment = comment.comment ?: oldComment.comment
                )
            )
            webSocketHandler.broadcastUpdate("UPDATE_COMMENT", updatedComment.jobCardId!!)
            updatedComment.toComment()
        } catch (e: Exception) {
            throw e
        }
    }

    fun deleteComment(commentId: String): Boolean {
        return try {
            val comment =
                commentRepository.findById(UUID.fromString(commentId)).orElseThrow { Exception("Comment not found.") }
            commentRepository.delete(comment)
            webSocketHandler.broadcastUpdate("DELETE_COMMENT", comment.jobCardId!!)
            true
        } catch (e: Exception) {
            throw e
        }
    }


    fun NewComment.toJobCardComments(): JobCardComments {
        val jobCard = jobCardRepository.findById(this.jobCardId).orElseThrow { Exception("Job Card not found.") }
        val employee = this.employeeId.let { employeeId ->
            employeeRepository.findById(employeeId).orElseThrow { Exception("Employee not found.") }
        }
        return JobCardComments(
            jobCardId = jobCard,
            comment = this.comment,
            employee = employee
        )
    }

    fun JobCardComments.toComment(): Comment {
        return Comment(
            commentId = this.jobCardCommentId,
            jobCardId = this.jobCardId?.jobId!!,
            employeeId = this.employee?.employeeId,
            comment = this.comment
        )
    }

}