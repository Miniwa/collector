package se.miniwa.fourchan.collector;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "thread")
public class ThreadDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private BoardDao board;

    @Column(name = "archived", nullable = false)
    private boolean archived;

    @Column(name = "archived_date")
    private Instant archivedDate;

    @Column(name = "closed", nullable = false)
    private boolean closed;

    @Column(name = "sticky", nullable = false)
    private boolean sticky;

    @Column(name = "bump_limited", nullable = false)
    private boolean bumpLimited;

    @Column(name = "image_limited", nullable = false)
    private boolean imageLimited;

    @Column(name = "subject")
    private String subject;

    @Column(name = "semantic_url")
    private String semanticUrl;

    @Column(name = "tag")
    private String tag;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BoardDao getBoard() {
        return board;
    }

    public void setBoard(BoardDao board) {
        this.board = board;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public Instant getArchivedDate() {
        return archivedDate;
    }

    public void setArchivedDate(Instant archivedDate) {
        this.archivedDate = archivedDate;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isBumpLimited() {
        return bumpLimited;
    }

    public void setBumpLimited(boolean bumpLimited) {
        this.bumpLimited = bumpLimited;
    }

    public boolean isImageLimited() {
        return imageLimited;
    }

    public void setImageLimited(boolean imageLimited) {
        this.imageLimited = imageLimited;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSemanticUrl() {
        return semanticUrl;
    }

    public void setSemanticUrl(String semanticUrl) {
        this.semanticUrl = semanticUrl;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
