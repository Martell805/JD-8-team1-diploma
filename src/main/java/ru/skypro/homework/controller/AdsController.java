package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdsService;
import ru.skypro.homework.service.CommentService;
import ru.skypro.homework.service.VerificationUserService;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
@Tag(name = "Объявления")
@SecurityRequirement(name = "basicAuth")
public class AdsController {
    private final AdsService adsService;
    private final CommentService commentService;
    private final VerificationUserService verificationUserService;

    @Operation(summary = "getALLAds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAds.class)))
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        return ResponseEntity.ok(adsService.getAllAds());
    }

    @Operation(summary = "addAds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class))),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Ads> addAds(@RequestPart(value = "properties") CreateAds properties,
                                      @RequestPart(value = "image") MultipartFile image, Authentication authentication) throws IOException {
        return ResponseEntity.ok(adsService.addAds(properties, image, authentication.getName()));
    }

    @Operation(summary = "getAdsMe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseWrapperAds.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping("/me")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<ResponseWrapperAds> getAllMeAds(Authentication authentication) {
        return ResponseEntity.ok(adsService.getAdsMe(authentication.getName()));
    }

    @Operation(summary = "getComments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseWrapperComment.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/comments")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<ResponseWrapperComment> getComment(@PathVariable Integer id) {
        return ResponseEntity.ok(commentService.getAllCommentsByAd(id));
    }

    @Operation(summary = "addComments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PostMapping("/{id}/comments")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @RequestBody Comment comments,
                                              Authentication authentication) {
        return ResponseEntity.ok(commentService.addComment(id, comments, authentication));
    }

    @Operation(summary = "deleteComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{adId}/comments/{commentId}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Void> deleteComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId,
                                              Authentication authentication) {

        if(!verificationUserService.verifyUsersCommentOrAdmin(commentId, authentication)){
            return ResponseEntity.badRequest().build();
        }

        commentService.deleteComment(adId, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "getComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{adId}/comments/{commentId}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Comment> getComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getComment(adId, commentId));
    }

    @Operation(summary = "updateComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping("/{adId}/comments/{commentId}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Comment> updateComments(@PathVariable Integer adId,
                                                  @PathVariable Integer commentId,
                                                  @RequestBody Comment comment,
                                                  Authentication authentication) {

        if(!verificationUserService.verifyUsersCommentOrAdmin(commentId, authentication)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(commentService.updateComment(adId, commentId, comment, authentication.getName()));
    }

    @Operation(summary = "removeAds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class))),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Void> removeAds(@PathVariable Integer id,
                                          Authentication authentication) {

        if(!verificationUserService.verifyUsersAdsOrAdmin(id, authentication)){
            return ResponseEntity.badRequest().build();
        }

        return adsService.deleteAds(id);
    }

    @Operation(summary = "getFullAd")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<FullAds> getFullAd(@PathVariable Integer id) {
        return ResponseEntity.ok(adsService.getFullAds(id));
    }

    @Operation(summary = "updateAds")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class))),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found"),
    })
    @PatchMapping("/{id}")
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<Ads> updateAds(@PathVariable Integer id,
                                         @RequestBody CreateAds createAds,
                                         Authentication authentication) {

        if(!verificationUserService.verifyUsersAdsOrAdmin(id, authentication)){
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(adsService.updateAds(id, createAds));
    }

    @Operation(summary = "updateAdsPoster")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "{id}/image",
                    produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE},
                    consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<byte[]> updatePoster(@PathVariable("id") Integer adsId,
                                              @RequestPart MultipartFile image,
                                               Authentication authentication) throws IOException {

        if(!verificationUserService.verifyUsersAdsOrAdmin(adsId, authentication)){
            return ResponseEntity.badRequest().build();
        }

        Pair<byte[], String> pair = adsService.updatePosterOfAds(adsId, image);
        return read(pair);
    }

    @Operation(
            summary = "getPoster",
            description = "Возвращает данные постера для объявления",
            tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "{adsId}/image", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @RolesAllowed({"USER", "ADMIN"})
    public ResponseEntity<byte[]> getPoster(
            @Parameter(in = ParameterIn.PATH, description = "ID объявления")
            @PathVariable("adsId") Integer idAds) {
        Pair<byte[], String> pair = adsService.getPoster(idAds);
        return read(pair);
    }

    private ResponseEntity<byte[]> read(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }
}
