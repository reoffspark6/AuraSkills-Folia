package dev.aurelium.auraskills.bukkit.scheduler;

import dev.aurelium.auraskills.bukkit.AuraSkills;
import dev.aurelium.auraskills.common.scheduler.Scheduler;
import dev.aurelium.auraskills.common.scheduler.Task;
import dev.aurelium.auraskills.common.scheduler.TaskRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class BukkitScheduler extends Scheduler {

    private final AuraSkills plugin;

    public BukkitScheduler(AuraSkills plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public Task executeSync(Runnable runnable) {
        //BukkitTask task = plugin.getServer().getScheduler().runTask(plugin, runnable);
        ScheduledTask taskz = plugin.getServer().getGlobalRegionScheduler().run(plugin, task -> { runnable.run(); });
        return new BukkitTaskWrapper(taskz);
    }

    @Override
    public Task scheduleSync(Runnable runnable, long delay, TimeUnit timeUnit) {
        //BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, timeUnit.toMillis(delay) / 50);
        ScheduledTask taskz = plugin.getServer().getGlobalRegionScheduler().runDelayed(plugin, task -> { runnable.run(); }, timeUnit.toMillis(delay) / 50);
        return new BukkitTaskWrapper(taskz);
    }

    @Override
    public Task timerSync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        //BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, timeUnit.toMillis(delay) / 50, timeUnit.toMillis(period) / 50);
        long delayaux = 1;
        if((timeUnit.toMillis(delay) / 50) > 0){
            delayaux = timeUnit.toMillis(delay) / 50;
        }
        ScheduledTask bukkitTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> { runnable.run(); }, delayaux, timeUnit.toMillis(period) / 50);
        Task task = new BukkitTaskWrapper(bukkitTask);
        runnable.injectTask(task);
        return task;
    } //getGlobalRegionScheduler getAsyncScheduler

    @Override
    public Task timerAsync(TaskRunnable runnable, long delay, long period, TimeUnit timeUnit) {
        //BukkitTask bukkitTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, runnable, timeUnit.toMillis(delay) / 50, timeUnit.toMillis(period) / 50);
        long delayaux = 1;
        if((timeUnit.toMillis(delay) / 50) > 0){
            delayaux = timeUnit.toMillis(delay) / 50;
        }
        ScheduledTask bukkitTask = plugin.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin, task -> { runnable.run(); }, delayaux, timeUnit.toMillis(period) / 50);
        Task task = new BukkitTaskWrapper(bukkitTask);
        runnable.injectTask(task);
        return task;
    }
}
