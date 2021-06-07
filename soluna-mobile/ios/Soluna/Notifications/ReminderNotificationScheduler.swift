import Foundation
import Shared

class ReminderNotificationScheduler {
    static func scheduleNotifications(completionHandler: @escaping () -> Void = {}) {
        SwiftKotlinBridge().getReminderNotificationList { reminderNotifications in
            cancelNotifications()
            if (reminderNotifications != nil) {
                reminderNotifications?.forEach { reminderNotification in
                    scheduleReminderNotification(reminderNotification)
                }
            }
            completionHandler()
        }
    }

    private static func scheduleReminderNotification(_ reminderNotification: ReminderNotification) {
        let eventType = reminderNotification.type.text()
        let locationLabel = reminderNotification.locationLabel
        let timeZone = TimeZone(identifier:  reminderNotification.timeZone) ?? TimeZone.current  // TODO error handle timezone
        let eventTime = reminderNotification.eventTime.toDisplayTime(timeZone: timeZone)

        let identifier = "\(reminderNotification.hash())"
        let message = "Upcoming \(eventType) in \(locationLabel) will be at \(eventTime)"
        let dateComponents = SwiftKotlinBridge().nsDateComponents(instant: reminderNotification.notificationTime, timeZone: timeZone)
        
        let userDefaults = UserDefaults(suiteName: "reminderNotifications")!
        var existingAlarms = userDefaults.array(forKey: "existingAlarmIdentifiers") ?? []
        existingAlarms.append(identifier)
        userDefaults.setValue(existingAlarms, forKey: "existingAlarmIdentifiers")

        NotificationManager.scheduleNotification(identifier: identifier, message: message, dateComponents: dateComponents)
    }
    
    private static func cancelNotifications() {
        let identifiers = UserDefaults(suiteName: "reminderNotifications")?.stringArray(forKey: "existingAlarmIdentifiers") ?? []
        NotificationManager.cancelNotifications(identifiers: identifiers)
    }
}
