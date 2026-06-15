package study.data_jpa;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
  @GetMapping("/hello")
  public int hello() {
    Test test = new Test();

    test.setNum(1);
    return test.getNum();
  }

  @Getter
  @Setter
  static class Test {
    private int num;
  }
}
