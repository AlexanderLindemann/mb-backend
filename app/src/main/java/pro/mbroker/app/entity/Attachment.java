package pro.mbroker.app.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
public class Attachment extends BaseEntity {

    @Column
    private String name;

    @Column(name = "size_bytes")
    private long sizeBytes;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "content_md5")
    private String contentMd5;

    @Column(name = "external_storage_id")
    private long externalStorageId;

}
