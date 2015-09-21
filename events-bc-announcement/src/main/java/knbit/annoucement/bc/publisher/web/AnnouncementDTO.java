package knbit.annoucement.bc.publisher.web;

import com.google.common.base.Preconditions;
import knbit.annoucement.bc.config.Publishers;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import java.util.Collection;

/**
 * Created by novy on 03.04.15.
 */
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnnouncementDTO {

    private Collection<String> publishers;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String imageUrl;

    public AnnouncementDTO(Collection<String> publishers, String title, String content, String imageUrl) {
        setPublishers(publishers);
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }


    private void setPublishers(Collection<String> publishers) {
        Preconditions.checkArgument(containsOnlyAllowedPublishers(publishers));
        this.publishers = publishers;
    }

    private boolean containsOnlyAllowedPublishers(Collection<String> publishers) {
        return Publishers.stringValues().containsAll(publishers);
    }
}