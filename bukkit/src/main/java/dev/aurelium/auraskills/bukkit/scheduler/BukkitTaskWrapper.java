package dev.aurelium.auraskills.bukkit.scheduler;

import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskStatus;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

public class BukkitTaskWrapper implements Task {

    //private final BukkitTask bukkitTask;
    private final ScheduledTask bukkitTask;

    public BukkitTaskWrapper(ScheduledTask bukkitTask) {
        this.bukkitTask = bukkitTask;
    }

    @Override
    public TaskStatus getStatus() {
        if (bukkitTask.isCancelled()) {
            return TaskStatus.STOPPED;
        } else {
            return TaskStatus.SCHEDULED;
        }
    }

    @Override
    public void cancel() {
        //Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        bukkitTask.cancel();
    }
}
