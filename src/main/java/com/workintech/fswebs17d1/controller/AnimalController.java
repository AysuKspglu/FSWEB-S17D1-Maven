package com.workintech.fswebs17d1.controller;

import com.workintech.fswebs17d1.entity.Animal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/workintech")
public class AnimalController {

    @Value("${course.name}")
    private String courseName;

    @Value("${project.developer.fullname}")
    private String developerName;

    // Map<Integer, Animal>
    private final Map<Integer, Animal> animals = new ConcurrentHashMap<>();

    // İsteğe bağlı bilgi endpoint'i (Value ile okunan)
    @GetMapping("/info")
    public Map<String, String> info() {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("course.name", courseName);
        m.put("project.developer.fullname", developerName);
        return m;
    }

    // [GET] /workintech/animal  -> tüm kayıtlar
    @GetMapping("/animal")
    public List<Animal> getAll() {
        return new ArrayList<>(animals.values());
    }

    // [GET] /workintech/animal/{id} -> tek kayıt
    @GetMapping("/animal/{id}")
    public ResponseEntity<Animal> getById(@PathVariable Integer id) {
        Animal a = animals.get(id);
        return (a != null) ? ResponseEntity.ok(a)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // [POST] /workintech/animal -> JSON body ile ekle (test 200 OK bekliyor)
    // Body örn: {"id":2,"name":"kedi"}
    @PostMapping(value = "/animal",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Animal> add(@RequestBody Animal body) {
        if (body == null || body.getId() == null || body.getName() == null) {
            return ResponseEntity.badRequest().build(); // 400
        }
        if (animals.containsKey(body.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
        }
        animals.put(body.getId(), body);
        return ResponseEntity.ok(body); // 200 (test böyle istiyor)
    }

    // [PUT] /workintech/animal/{id} -> path id esas, body.id path id'ye set edilir
    @PutMapping(value = "/animal/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Animal> update(@PathVariable Integer id, @RequestBody Animal body) {
        if (!animals.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
        }
        if (body == null || body.getName() == null) {
            return ResponseEntity.badRequest().build(); // 400
        }
        body.setId(id);
        animals.put(id, body);
        return ResponseEntity.ok(body); // 200
    }

    // [DELETE] /workintech/animal/{id} -> bulunduysa 200, yoksa 404
    @DeleteMapping("/animal/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return (animals.remove(id) != null)
                ? ResponseEntity.ok().build() // 200 (test böyle istiyor)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404
    }
}
