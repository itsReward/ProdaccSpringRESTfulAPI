package org.prodacc.webapi.controllers

import org.prodacc.webapi.services.CommentService
import org.prodacc.webapi.services.dataTransferObjects.Comment
import org.prodacc.webapi.services.dataTransferObjects.NewComment
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/comments/")
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping("/all")
    fun getComments() : Iterable<Comment> = commentService.getAllComments()

    @GetMapping("/get/{commentId}")
    fun getCommentById(@PathVariable commentId: String) : Comment = commentService.getCommentById(commentId)

    @GetMapping("/get/jobCard/{jobCardId}")
    fun getJobCardComments(@PathVariable jobCardId: String) : Iterable<Comment> = commentService.getAllJobCardComments(jobCardId)

    @PostMapping("/new")
    fun createComment(@RequestBody comment: NewComment) : Comment = commentService.createComment(comment)

    @PutMapping("/update/{commentId}")
    fun updateComment(@RequestBody comment: Comment, @PathVariable commentId: String) : Comment = commentService.updateComment(comment, commentId)

    @DeleteMapping("/delete/{commentId}")
    fun deleteComment(@PathVariable commentId: String) : Boolean = commentService.deleteComment(commentId)
}