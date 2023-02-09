package pro.mbroker.app.model.document;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column(name = "size_bytes")
    private long sizeBytes;

    @Column(name = "external_storage_id")
    private String externalStorageId;

    @CreatedDate
    @Column(name = "created_at")
    private ZonedDateTime createdAt;

}
