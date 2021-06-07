import SwiftUI
import Shared

struct HomeView: View {
    @StateObject
    private var observableModel = ObservableHomeViewModel()
        
    var body: some View {
        VStack {
            HomeContent(
                state: observableModel.state,
                onNavigateToLocationList: observableModel.onNavigateToLocationList,
                onNavigateToReminderList: observableModel.onNavigateToReminderList
            )
            NavigationLink(destination: LocationListView(), isActive: $observableModel.navigateToLocationList) { EmptyView() }
            NavigationLink(destination: ReminderListView(), isActive: $observableModel.navigateToReminderList) { EmptyView() }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { ToolbarItem(placement: .principal) { Text("Soluna") } }
        .bindModel(observableModel)
    }
}

struct HomeContent : View {
    let state: HomeViewModel.State

    let onNavigateToLocationList: () -> Void
    let onNavigateToReminderList: () -> Void

    var body: some View {
        VStack {
            switch state {
            case is HomeViewModel.StateLoading:
                Text("Loading...")
            case is HomeViewModel.StateNoLocationSelected:
                Text("No location is selected!")
            case is HomeViewModel.StatePopulated:
                let populatedState = state as! HomeViewModel.StatePopulated
                let timeZone = TimeZone.init(identifier: populatedState.timeZone.id) ?? TimeZone.current // TODO error handling here
                Text(populatedState.locationName)
                Text(populatedState.currentTime.toDisplayTime(timeZone: timeZone))
                Text(populatedState.timeZone.id)
                Text("Sunrise: \(populatedState.sunriseTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Sunset: \(populatedState.sunsetTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Moonrise: \(populatedState.moonriseTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Moonset: \(populatedState.moonsetTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
            default:
                EmptyView()
            }
            Button("Locations") {
                onNavigateToLocationList()
            }
            Button("Reminders") {
                onNavigateToReminderList()
            }
        }
    }
}
