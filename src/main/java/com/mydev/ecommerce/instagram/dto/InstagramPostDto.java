


package com.mydev.ecommerce.instagram.dto;

public class InstagramPostDto {

    private String id;
    private String caption;
    private String mediaType;
    private String mediaUrl;
    private String thumbnailUrl;
    private String permalink;
    private String timestamp;
    private String alt;
    private boolean video;

    public InstagramPostDto() {
    }

    public InstagramPostDto(
            String id,
            String caption,
            String mediaType,
            String mediaUrl,
            String thumbnailUrl,
            String permalink,
            String timestamp,
            String alt,
            boolean video
    ) {
        this.id = id;
        this.caption = caption;
        this.mediaType = mediaType;
        this.mediaUrl = mediaUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.permalink = permalink;
        this.timestamp = timestamp;
        this.alt = alt;
        this.video = video;
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getPermalink() {
        return permalink;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAlt() {
        return alt;
    }

    public boolean isVideo() {
        return video;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}