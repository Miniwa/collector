package se.miniwa.fourchan.collector;

import com.google.api.client.http.HttpResponseException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AbstractScheduledService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import se.miniwa.fourchan.FourChanImage;
import se.miniwa.fourchan.FourChanModifiedDate;
import se.miniwa.fourchan.FourChanPost;
import se.miniwa.fourchan.FourChanThread;
import se.miniwa.fourchan.api.FourChanClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CollectorService extends AbstractScheduledService {
    private static final Logger logger = LogManager.getLogger(CollectorService.class);
    private String board;
    private FourChanClient client;
    private SessionFactory sessionFactory;
    private ImmutableList<FourChanModifiedDate> oldThreads;
    private List<Integer> threadsToCollect;

    public CollectorService(String board, FourChanClient client, SessionFactory sessionFactory) {
        this.board = Preconditions.checkNotNull(board);
        this.client = Preconditions.checkNotNull(client);
        this.sessionFactory = Preconditions.checkNotNull(sessionFactory);
        this.oldThreads = ImmutableList.of();
        this.threadsToCollect = new ArrayList<>();
    }

    @Override
    protected void runOneIteration() {
        ImmutableList<FourChanModifiedDate> activeThreads;
        try {
            activeThreads = client.getModifiedDates(board);
        } catch(IOException e) {
            logger.warn("Could not fetch active threads.", e);
            return;
        }

        for (FourChanModifiedDate oldThread : oldThreads) {
            if (activeThreads.stream().noneMatch(
                    (FourChanModifiedDate activeThread) -> oldThread.getNumber() == activeThread.getNumber())) {
                threadsToCollect.add(oldThread.getNumber());
            }
        }

        if (threadsToCollect.size() > 0) {
            logger.debug(String.format("Attempting to collect %d archived threads..", threadsToCollect.size()));
            Iterator<Integer> threadIterator = threadsToCollect.iterator();
            while (threadIterator.hasNext()) {
                int threadNumber = threadIterator.next();
                try {
                    FourChanThread thread = client.getThread(board, threadNumber);
                    if (thread.isArchived() || thread.isClosed()) {
                        logger.debug(String.format("Collecting %d posts.", thread.getReplies().size() + 1));

                        Session session = sessionFactory.openSession();
                        Transaction transaction = session.beginTransaction();
                        BoardDao boardDao = (BoardDao)session.createQuery("select * from board where name = :name")
                                .setParameter("name", board).uniqueResult();
                        ThreadDao threadDao = copyThread(boardDao, thread);
                        PostDao opDao = copyPost(threadDao, thread.getOp());
                        session.save(threadDao);
                        session.save(opDao);

                        for (FourChanPost post : thread.getReplies()) {
                            PostDao postDao = copyPost(threadDao, post);
                            session.save(postDao);
                        }

                        logger.debug("Committing transaction..");
                        transaction.commit();
                        threadIterator.remove();
                    } else {
                        logger.debug("Waiting for thread to become archived.");
                    }
                } catch (IOException e) {
                    if (e instanceof HttpResponseException && ((HttpResponseException)e).getStatusCode() == 404) {
                        logger.debug(String.format("Thread %s was removed by janitor.", threadNumber));
                        threadIterator.remove();
                    } else {
                        logger.error("Could not retrieve thread.", e);
                    }
                } catch (HibernateException e) {
                    logger.error("Could not save data.", e);
                }
            }
        }
        oldThreads = activeThreads;
        logger.debug("Thread table was updated.");
    }

    @Override
    protected Scheduler scheduler() {
        return AbstractScheduledService.Scheduler.newFixedDelaySchedule(0, 120, TimeUnit.SECONDS);
    }

    private ThreadDao copyThread(BoardDao boardDao, FourChanThread thread) {
        ThreadDao threadDao = new ThreadDao();
        threadDao.setBoard(boardDao);
        threadDao.setArchived(thread.isArchived());
        threadDao.setArchivedDate(thread.getArchivedDate());
        threadDao.setClosed(thread.isClosed());
        threadDao.setSticky(thread.isSticky());
        threadDao.setBumpLimited(thread.isBumpLimited());
        threadDao.setImageLimited(thread.isImageLimited());
        threadDao.setSubject(thread.getSubject());
        threadDao.setSemanticUrl(thread.getSemanticUrl());
        threadDao.setTag(thread.getTag());
        return threadDao;
    }

    private PostDao copyPost(ThreadDao threadDao, FourChanPost post) {
        PostDao postDao = new PostDao();
        postDao.setThread(threadDao);
        postDao.setNumber(post.getNumber());
        postDao.setSubmittedDate(post.getSubmittedDate());
        postDao.setName(post.getName());
        postDao.setTripcode(post.getTripcode());
        postDao.setPostId(post.getId());
        postDao.setPassSinceYear(post.getPassSinceYear());
        postDao.setCapcode(post.getCapcode());
        postDao.setComment(post.getComment());

        FourChanImage image = post.getImage();
        if (image != null) {
            postDao.setImage(copyImage(image));
        } else {
            postDao.setImage(null);
        }
        return postDao;
    }

    private ImageDao copyImage(FourChanImage image) {
        ImageDao imageDao = new ImageDao();
        imageDao.setFilename(image.getFilename());
        imageDao.setOriginalFilename(image.getOriginalFilename());
        imageDao.setWidth(image.getWidth());
        imageDao.setHeight(image.getHeight());
        imageDao.setSize(image.getSize());
        imageDao.setMd5(image.getMd5());
        imageDao.setDeleted(image.isDeleted());
        imageDao.setSpoiler(image.isSpoiler());
        return imageDao;
    }
}
