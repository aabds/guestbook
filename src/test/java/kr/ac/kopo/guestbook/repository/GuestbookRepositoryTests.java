package kr.ac.kopo.guestbook.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import kr.ac.kopo.guestbook.entity.Guestbook;
import kr.ac.kopo.guestbook.entity.QGuestbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
public class GuestbookRepositoryTests {
    @Autowired
    private GuestbookRepository guestbookRepository;


    @Test
    public void insertDummies() {
        IntStream.rangeClosed(1, 300).forEach(i -> {
            Guestbook guestbook = Guestbook.builder()
                    .title("Title..." + i)
                    .content("Content..." + i)
                    .writter("writer" + (i % 10))
                    .build();
            System.out.println(guestbookRepository.save(guestbook));
        });
    }

    @Test
    public void updateTest(){
        Optional<Guestbook> result = guestbookRepository.findById(300L);

        if(result.isPresent()){
            Guestbook guestbook = result.get();
            guestbook.changeTitle("Changed Title...");
            guestbook.changeContent("Changed Content...");
            guestbookRepository.save(guestbook);
        }
    }


    //단일 검색
    @Test
    public void testQuery1(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("gno").descending());
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "7";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expression = qGuestbook.title.contains(keyword);
        builder.and(expression);
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }


    //다중검색
    @Test
    public void testQuery2(){
        Pageable pageable = PageRequest.of(0,10, Sort.by("gno").ascending());
        QGuestbook qGuestbook = QGuestbook.guestbook;
        String keyword = "7";
        BooleanBuilder builder = new BooleanBuilder();
        BooleanExpression expTitle = qGuestbook.title.contains(keyword);
        BooleanExpression expWriter = qGuestbook.writter.contains(keyword);
        BooleanExpression expAll = expTitle.and(expWriter);
        builder.and(expAll);
        builder.and(qGuestbook.gno.gt(50L));
        Page<Guestbook> result = guestbookRepository.findAll(builder, pageable);
        result.stream().forEach(guestbook -> {
            System.out.println(guestbook);
        });
    }

}
