import UIKit

class NotificationManager {
    static func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { success, error in
            if let error = error {
                NSLog("Error authorizing notifications! \(error.localizedDescription)")
            }
            
            NSLog("Notification permission status: \(success)")
        }
    }
    
    static func scheduleNotification(identifier: String, message: String, dateComponents: DateComponents) {
        let content = UNMutableNotificationContent()
        content.body = message

        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: false)
        
        let request = UNNotificationRequest(identifier: identifier, content: content, trigger: trigger)
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                NSLog("An error occurred scheduling notification: \(error.localizedDescription)")
            }
        }
    }
    
    static func cancelNotifications(identifiers: [String]) {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: identifiers)
    }
}
