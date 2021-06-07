import BackgroundTasks

class BackgroundNotificationScheduler {
    static func configure() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.russhwolf.soluna.ios.ScheduleNotifications", using: nil) { task in
            
            ReminderNotificationScheduler.scheduleNotifications {
                task.setTaskCompleted(success: true)
            }
            
            submitBGRequest()
        }
    }
    
    static func submitBGRequest() {
        let request = BGProcessingTaskRequest(identifier: "com.russhwolf.soluna.ios.ScheduleNotifications")
        request.earliestBeginDate = Date().addingTimeInterval(60 * 60 * 24)
        do {
            try BGTaskScheduler.shared.submit(request)
        } catch (let e) {
            // TODO retry/rechedule
            NSLog("An error occurred scheduling BG sync: \(e.localizedDescription)")
        }
    }
}
