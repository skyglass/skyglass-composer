package com.killrvideo.service.video.dao;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.QueryProvider;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.killrvideo.dse.dto.CustomPagingState;
import com.killrvideo.dse.dto.ResultListPage;
import com.killrvideo.dse.dto.Video;
import com.killrvideo.service.video.dto.LatestVideo;
import com.killrvideo.service.video.dto.LatestVideosPage;
import com.killrvideo.service.video.dto.UserVideo;

/**
 * Operations against DSE (Cassandra Part) to implements Video operations
 * 
 * @author DataStax Developer Advocates team.
 */
@Dao
public interface VideoCatalogDseDao {
    
    /**
     * Will select a video from its PK video id in 'videos' table.
     * 
     * @implNote : This method will be entirely generated by the driver, not specific code
     * 
     * @param videoid
     *      current videoid
     * @return
     *      bean video mapped
     */
    @Select
    CompletionStage<Video> getVideoByIdAsync(UUID videoid);
    
    /**
     * Will insert a record in 'videos', 'user_videos' and 'latest_videos'.
     * As such we cannot leverage on @Insert annotation we would like to use batch.
     * 
     * @param videoid
     *      current videoid
     * @return
     *      bean video mapped
     */
    @QueryProvider(
            providerClass = VideoCatalogDseDaoQueryProvider.class, 
            entityHelpers = {Video.class, LatestVideo.class, UserVideo.class})
    CompletionStage<Void> insertVideoAsync(Video video);
    
    /**
     * Latest video partition key is the Date. As such we need to perform a query per date. As the user
     * ask for a number of video on a given page we may have to trigger several queries, on for each day.
     * To do it we implement a couple 
     * 
     * For those of you wondering where the call to fetchMoreResults() is take a look here for an explanation.
     * https://docs.datastax.com/en/drivers/java/3.2/com/datastax/driver/core/PagingIterable.html#getAvailableWithoutFetching--
     * 
     * Quick summary, when getAvailableWithoutFetching() == 0 it automatically calls fetchMoreResults()
     * We could use it to force a fetch in a "prefetch" scenario, but that is not what we are doing here.
     * 
     * @throws ExecutionException
     *      error duing invoation 
     * @throws InterruptedException
     *      error in asynchronism 
     */
    @QueryProvider(
            providerClass = VideoCatalogDseDaoQueryProvider.class, 
            entityHelpers = {Video.class, LatestVideo.class, UserVideo.class})
    LatestVideosPage getLatestVideoPreviews(CustomPagingState cpState, 
            int pageSize, 
            Optional<Date> startDate, 
            Optional<UUID> startVid);
    
    /**
     * Read a page of video preview for a user.
     * 
     * @param userId
     *      user unique identifier
     * @param startingVideoId
     *      starting video if paging
     * @param startingAddedDate
     *      added date if paging
     * @param pagingState
     *      paging state if paging
     * @return
     *      requested video (page)
     */
    @QueryProvider(
            providerClass = VideoCatalogDseDaoQueryProvider.class, 
            entityHelpers = {Video.class, LatestVideo.class, UserVideo.class})
    public CompletionStage< ResultListPage <UserVideo> > getUserVideosPreview(UUID userId, 
            Optional<UUID> startingVideoId, 
            Optional<Date> startingAddedDate,
            Optional<Integer> pageSize,
            Optional<String>  pagingState);

}
