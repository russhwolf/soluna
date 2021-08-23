import SwiftUI
import Shared

struct HomeView: View {
    @StateObject
    private var observableModel = ObservableHomeViewModel()
        
    var body: some View {
        VStack {
            HomeContent(
                state: observableModel.state
            )
            NavigationLink(destination: SettingsView(), isActive: $observableModel.navigateToSettings) { EmptyView() }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar {
            ToolbarItem(placement: .principal) {
                Text(
                    observableModel.state is HomeViewModel.StatePopulated
                        ? (observableModel.state as! HomeViewModel.StatePopulated).locationName
                        : "Soluna"
                ).font(.title)
            }
            ToolbarItem(placement: .navigationBarTrailing) {
                Image(systemName: "gear").onTapGesture {
                    observableModel.onNavigateToSettings()
                }
            }
        }
        .bindModel(observableModel)
    }
}

struct HomeContent : View {
    let state: HomeViewModel.State

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
                Text("Sunrise: \(populatedState.sunriseTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Sunset: \(populatedState.sunsetTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Moonrise: \(populatedState.moonriseTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Moonset: \(populatedState.moonsetTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                SunMoonTimesGraphic(
                    currentTime: populatedState.currentTime,
                    sunriseTime: populatedState.sunriseTime,
                    sunsetTime: populatedState.sunsetTime,
                    moonriseTime: populatedState.moonriseTime,
                    moonsetTime: populatedState.moonsetTime,
                    timeZone: populatedState.timeZone
                )
                Text("Using time zone \(populatedState.timeZone.id)")
                    .font(.caption)
            default:
                fatalError("Invalid state: \(state)")
            }
        }
    }
}
