package com.frcalderon.commands.repository;

import com.frcalderon.commands.model.UpdateStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UpdateStockRepository extends JpaRepository<UpdateStock, Long> {

    List<UpdateStock> findAllBySent(boolean sent);
}
