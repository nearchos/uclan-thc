package uk.ac.uclan.thc.model;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * @author Nearchos Paspallis on 26/10/2015 / 08:13.
 */
public class TimedQuestion implements Serializable
{
    public static final Logger log = Logger.getLogger(TimedQuestion.class.getCanonicalName());

    private final String uuid;
    private final String title;
    private final String createdBy;
    private final String categoryUUID;
    private final String body;
    private final String imageUrl;

    public TimedQuestion(final String uuid,
                    final String title,
                    final String createdBy,
                    final String categoryUUID,
                    final String body,
                    final String imageUrl)
    {
        this.uuid = uuid;
        this.title = title;
        this.createdBy = createdBy;
        this.categoryUUID = categoryUUID;
        this.body = body;
        this.imageUrl = imageUrl;
    }

    public String getUUID()
    {
        return uuid;
    }

    public String getTitle()
    {
        return title;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public String getCategoryUUID()
    {
        return categoryUUID;
    }

    public String getBody()
    {
        return body;
    }

    public String getImageUrl()
    {
        return imageUrl;
    }

    @Override public String toString()
    {
        return "TimedQuestion{" +
                "uuid='" + uuid + '\'' +
                ", title='" + title + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", categoryUUID='" + categoryUUID + '\'' +
                ", body='" + body + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}