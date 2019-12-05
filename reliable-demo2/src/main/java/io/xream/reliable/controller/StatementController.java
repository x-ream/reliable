package io.xream.reliable.controller;


import io.xream.reliable.bean.CatStatement;
import io.xream.reliable.repository.CatStatementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import x7.core.web.ViewEntity;

@RestController
@RequestMapping("/statement")
public class StatementController {

    @Autowired
    private CatStatementRepository catStatementRepository;

    @RequestMapping("/create")
    public ViewEntity create(@RequestBody CatStatement catStatement) {

        this.catStatementRepository.create(catStatement);

        return ViewEntity.ok();
    }

}
