package se.miniwa.fourchan.collector;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "post")
public class PostDao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "thread_id", nullable = false)
    private ThreadDao thread;

    @Column(name = "number", nullable = false, unique = true)
    private int number;

    @Column(name = "submitted_date", nullable = false)
    private Instant submittedDate;

    @Column(name = "name")
    private String name;

    @Column(name = "tripcode")
    private String tripcode;

    @Column(name = "post_id")
    private String postId;

    @Column(name = "pass_since_year")
    private Integer passSinceYear;

    @Column(name = "capcode")
    private String capcode;

    @Column(name = "comment")
    private String comment;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", unique = true)
    private ImageDao image;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ThreadDao getThread() {
        return thread;
    }

    public void setThread(ThreadDao thread) {
        this.thread = thread;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Instant getSubmittedDate() {
        return submittedDate;
    }

    public void setSubmittedDate(Instant submittedDate) {
        this.submittedDate = submittedDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTripcode() {
        return tripcode;
    }

    public void setTripcode(String tripcode) {
        this.tripcode = tripcode;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Integer getPassSinceYear() {
        return passSinceYear;
    }

    public void setPassSinceYear(Integer passSinceYear) {
        this.passSinceYear = passSinceYear;
    }

    public String getCapcode() {
        return capcode;
    }

    public void setCapcode(String capcode) {
        this.capcode = capcode;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ImageDao getImage() {
        return image;
    }

    public void setImage(ImageDao image) {
        this.image = image;
    }
}
