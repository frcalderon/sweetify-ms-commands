package com.frcalderon.commands.schedule;

import com.frcalderon.commands.service.UpdateStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    private UpdateStockService updateStockService;

    @Scheduled(cron = "0 0 1 ? * *")
    public void updateStockInProductsService() {
        updateStockService.sendRequestToUpdateStock();
    }
}
