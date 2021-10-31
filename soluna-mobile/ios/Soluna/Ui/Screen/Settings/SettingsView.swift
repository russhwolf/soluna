import SwiftUI
import Shared

struct SettingsView: View {
    @StateObject
    private var observableModel = ObservableSettingsViewModel()
    
    var body: some View {
        VStack {
            SettingsContent(
                onNavigateToLocationList: { observableModel.onNavigateToLocationList() },
                onNavigateToReminderList: { observableModel.onNavigateToReminderList() }
            )
            observableModel.locationListTrigger.createLink { LocationListView() }
            observableModel.reminderListTrigger.createLink { ReminderListView() }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { ToolbarItem(placement: .principal) { Text("Settings") } }
        .bindModel(observableModel)
    }
}

struct SettingsContent : View {
    let onNavigateToLocationList: () -> Void
    let onNavigateToReminderList: () -> Void

    var body: some View {
        List {
            Button("Locations", action: onNavigateToLocationList)
            Button("Reminders", action: onNavigateToReminderList)
        }
    }
}

struct SettingsContent_Previews : PreviewProvider {
    static var previews: some View {
        SettingsContent(
            onNavigateToLocationList: {},
            onNavigateToReminderList: {}
        ).screenPreview()
    }
}
