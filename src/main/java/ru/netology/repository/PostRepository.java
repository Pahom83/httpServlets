package ru.netology.repository;

import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
@Repository
// Stub
public class PostRepository {
    private final ConcurrentHashMap<Long, Post> posts = new ConcurrentHashMap<>();
    private final AtomicLong count = new AtomicLong(0);
    private final ConcurrentLinkedDeque<Long> deletedID = new ConcurrentLinkedDeque<>();
    public List<Post> all() {
        return List.copyOf(posts.values());
    }
    public Post getById(long id) {
        return posts.get(id);
    }

    public Post save(Post post) {
        if (post.getId() == 0) {
            long id;
            if (deletedID.size() == 0){
                count.getAndIncrement();
                id = count.longValue();
            } else {
                id = deletedID.getFirst();
            }
            post.setId(id);
            posts.putIfAbsent(id, post);
        } else {
            if (posts.containsKey(post.getId())) {
                posts.replace(post.getId(), post);
            }
        }
        return post;
    }

    public void removeById(long id) {
        if (posts.containsKey(id)) {
            posts.remove(id);
            deletedID.addLast(id);
        }
    }
}
