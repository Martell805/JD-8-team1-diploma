package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.impl.AdsServiceImpl;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@RestController
@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdsController {
    private final AdsServiceImpl adsServiceImpl;

    @Operation(summary = "getALLAds", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAds.class)))
    })
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseWrapperAds> getAllAds() {
        return ResponseEntity.ok(adsServiceImpl.getAllAds());
    }

    @Operation(summary = "addAds", tags = {"Объявления"})
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
    public ResponseEntity<Ads> addAds(@RequestPart CreateAds properties,
                                      @RequestPart MultipartFile image, Authentication authentication) {
        return ResponseEntity.ok(adsServiceImpl.addAds(properties, image, authentication.getName()));
    }

    @Operation(summary = "getAdsMe", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseWrapperAds.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    @GetMapping("/me")
    public ResponseEntity<ResponseWrapperAds> getAllMeAds(Authentication authentication) {
        return ResponseEntity.ok(adsServiceImpl.getAdsMe(authentication.getName()));
    }

    @Operation(summary = "getComments", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ResponseWrapperComment.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{id}/comments")
    public ResponseEntity<ResponseWrapperComment> getComment(@PathVariable Integer id) {
        return ResponseEntity.ok(commentServiceImpl.getAllCommentsByAd(id));
    }
    @Operation(summary = "addComments", tags = {"Объявления"})
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
    public ResponseEntity<Comment> addComment(@PathVariable Integer id,
                                              @RequestBody Comment comments,
                                              Authentication authentication) {
        return ResponseEntity.ok(commentServiceImpl.addComment(id, comments, authentication));
    }

    @Operation(summary = "deleteComment", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Integer adId,
                                                 @PathVariable Integer commentId) {
       commentServiceImpl.deleteComment(adId, commentId);
       return ResponseEntity.ok().build();
    }

    @Operation(summary = "getComment", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @GetMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable Integer adId,
                                              @PathVariable Integer commentId) {
        return ResponseEntity.ok(commentServiceImpl.getComment(adId, commentId));
    }

    @Operation(summary = "updateComment", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Comment.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Comment> updateComments(@PathVariable Integer adId,
                                                  @PathVariable Integer commentId,
                                                  @RequestBody Comment comment,
                                                  Authentication authentication) {
        return ResponseEntity.ok(commentServiceImpl.updateComment(adId, commentId, comment, authentication));
    }
    @Operation(summary = "removeAds", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class))),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Ads> removeAds(@PathVariable Integer id) {
        adsServiceImpl.deleteAds(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "getFullAd", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FullAds> getFullAd(@PathVariable Integer id) {
        return ResponseEntity.ok(adsServiceImpl.getFullAds(id));
    }

    @Operation(summary = "updateAds", tags = {"Объявления"})
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
    public ResponseEntity<Ads> updateAds(@PathVariable Integer id,
                                         @RequestBody CreateAds createAds) {
        return ResponseEntity.ok(adsServiceImpl.updateAds(id, createAds));
    }

    @Operation(
            summary = "getPoster",
            description = "Возвращает данные постера для объявления",
            tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @GetMapping(value = "{adsId}/image",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<byte[]> getPoster(
            @Parameter(in = ParameterIn.PATH, description = "ID объявления")
            @PathVariable("adsId") Integer idAds) {
        Pair<byte[], String> pair = adsServiceImpl.getPoster(idAds);
        return read(pair);
    }

    private ResponseEntity<byte[]> read(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }
}
