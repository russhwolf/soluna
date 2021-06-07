import UIKit
import SwiftUI
import Shared

@UIApplicationMain
class SceneDelegate: UIResponder, UIWindowSceneDelegate, UIApplicationDelegate {
    
    var window: UIWindow?
    
    func application(_ application: UIApplication, willFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {

        SwiftKotlinBridge().doInitKoin()

        BackgroundNotificationScheduler.configure()
        BackgroundNotificationScheduler.submitBGRequest()

        SwiftKotlinBridge().observeReminderNotificationList { event in
            ReminderNotificationScheduler.scheduleNotifications()
        }
        
        NotificationManager.requestPermission()

        return true
    }

    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        
        if let windowScene = scene as? UIWindowScene {
            let window = UIWindow(windowScene: windowScene)
            self.window = window
            window.rootViewController = UIHostingController(rootView: ContentView())
            window.makeKeyAndVisible()
        }
    }
}

