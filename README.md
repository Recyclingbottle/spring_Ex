## 0번 실습

### **1. Hello Spring Boot Application 만들어보기**

- 가장 기본적인 스프링 부트 애플리케이션을 구축하고 실행해 본다.
- **@RestController**와 **@GetMapping** 애노테이션을 사용하여 간단한 HTTP GET 요청을 처리한다.

간단한 설명

1. 스프링 부트 시작하기 프로젝트([https://start.spring.io/)를](https://start.spring.io/)%EB%A5%BC) 사용하여 새 스프링 부트 애플리케이션을 생성합니다.
2. **HelloController**라는 새로운 컨트롤러 클래스를 만들고, **/hello** 경로로 접근했을 때 "Hello, Spring Boot!" 문자열을 반환하는 메서드를 구현합니다.

[실행 화면]

![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/f6b34076-9e55-4714-a372-6960e8f86bee/Untitled.png)

[소스코드]

[`HelloSpringBootApplication.java`](http://HelloSpringBootApplication.java) 그대로 유지 

```jsx
package com.example.hello_spring_boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HelloSpringBootApplication {

	public static void main(String[] args) {
		SpringApplication.run(HelloSpringBootApplication.class, args);
	}
}
```

Controller 패키지 생성 후, 

`HelloController` 

```jsx
package com.example.hello_spring_boot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Boot!";
    }
}
```

### **2. 간단한 CRUD API 구현하기**

**목표**

- 스프링 부트를 사용하여 간단한 CRUD 기능을 가진 RESTful API를 구현합니다.
- In-memory 데이터베이스인 H2를 사용하여 데이터를 저장하고 관리합니다.

먼저, H2 와 관련된 Dependency 를 추가하였다. 

```java
dependencies { 
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'com.h2database:h2'
}
```

[`application.properties`](http://application.properties) 에도 필요한 내용을 추가하자

```java
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=asdf1234!
spring.datasource.platform=h2

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

Model 패키지 생성 후 간단한 CRUD 를 위한 ITEM 이라는 모델을 생성하였다

Item.java

ITEM Entitiy 는  id, name, description(String)

```java
package com.example.hello_spring_boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
```

jpa 를 이용하여 repository 패키지의 ItemRepository 를 만들었다

```java
package com.example.hello_spring_boot.repository;

import com.example.hello_spring_boot.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
```

`ItemService` 과 `ItemController` 만들어준다.

```java
package com.example.hello_spring_boot.service;

import com.example.hello_spring_boot.model.Item;
import com.example.hello_spring_boot.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item itemDetails) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));

        item.setName(itemDetails.getName());
        item.setDescription(itemDetails.getDescription());

        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        itemRepository.delete(item);
    }
}
```

```java
package com.example.hello_spring_boot.controller;

import com.example.hello_spring_boot.model.Item;
import com.example.hello_spring_boot.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Optional<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id);
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.createItem(item);
    }

    @PutMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody Item itemDetails) {
        return itemService.updateItem(id, itemDetails);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
    }
}
```

### 위에서 만든 내용을 POSTMAN 으로 테스트

1. CREATE
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/4e02d8bb-55e4-41aa-b2bf-46e4c31975bb/Untitled.png)
    
2. READ
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/fac125dd-fccd-4813-ba9a-ae6a35b1a4a2/Untitled.png)
    
3. UPDATE
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/fe61a855-9772-4e62-a653-c7941e9e9aec/Untitled.png)
    
4. DELETE 
5. 
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/a06ec2e0-6a9d-4b46-8860-2a9300d015f0/Untitled.png)
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/1de06d61-00ca-4996-9312-f6de7aac09b6/Untitled.png)
    
6. H2 Console 에서도 삽입, 삭제, 수정을 확인했습니다.
    
    ![Untitled](https://prod-files-secure.s3.us-west-2.amazonaws.com/d52326c2-82e1-4712-b3c2-04eb54222598/593c5d23-14ab-4ab3-bd1f-66e2a535c551/Untitled.png)
    
7. GitHub Repository
    
    https://github.com/Recyclingbottle/spring_Ex
