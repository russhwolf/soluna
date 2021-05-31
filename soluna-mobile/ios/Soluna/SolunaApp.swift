import SwiftUI
import Shared

@main
struct SolunaApp: App {
    init() {
        SwiftKotlinBridge().doInitKoin()
        
        SwiftKotlinBridge().getReminderNotificationList().subscribe(
            onEvent: { event in
                NSLog("Reminder notifications dirtied!")
                if let reminderNotifications = event as? [ReminderNotification] {
                    NSLog(reminderNotifications.description)
                } else {
                    NSLog("null")
                }
            },
            onError: { _ in },
            onComplete: { })
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

struct ContentView: View {
    var body: some View {
        NavigationView {
            HomeView()
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
