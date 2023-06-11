package com.frcalderon.commands.repository;

import com.frcalderon.commands.model.CommandProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommandProductRepository extends JpaRepository<CommandProduct, Long> {

    void deleteByCommandId(Long commandId);
}
