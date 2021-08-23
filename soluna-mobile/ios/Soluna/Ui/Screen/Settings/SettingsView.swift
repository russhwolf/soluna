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
            NavigationLink(destination: LocationListView(), isActive: $observableModel.navigateToLocationList) { EmptyView() }
            NavigationLink(destination: ReminderListView(), isActive: $observableModel.navigateToReminderList) { EmptyView() }
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
        VStack {
            Button("Locations", action: onNavigateToLocationList)
            Button("Reminders", action: onNavigateToReminderList)
        }
    }
}


