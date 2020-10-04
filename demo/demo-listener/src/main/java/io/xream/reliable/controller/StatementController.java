package io.xream.reliable.controller;


import io.xream.reliable.bean.CatStatement;
import io.xream.reliable.repository.CatStatementRepository;
import io.xream.x7.base.web.ViewEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/statement")

/**
 * @Author Sim
 */
public class StatementController {

    @Autowired
    private CatStatementRepository catStatementRepository;

    @RequestMapping("/create")
    public ViewEntity create(@RequestBody CatStatement catStatement) {

        this.catStatementRepository.create(catStatement);

        return ViewEntity.ok();
    }

}
