package pro.mbroker.app.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(of = "id")
public class Profile {  //TODO если не используем, то удалить.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "profile")
    private BaseMortgageInfo baseInfo;

    @ManyToMany
    @JoinTable(
            name = "profile_attachment",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private Set<Attachment> attachments;

    @Column(name = "organization_id")
    private Long organizationId;

    @CreatedDate
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by")
    private Long createdBy;

}
