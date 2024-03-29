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
            observableModel.settingsTrigger.createLink { SettingsView() }
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

struct HomeContent_Previews : PreviewProvider {
    private static let timeZone =
        Shared.TimeZone.Companion().of(zoneId: "America/New_York")
    
    static var previews: some View {
       HomeContent(state: HomeViewModel.StatePopulated(
            locationName: "Home",
            currentTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 11, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
            sunriseTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 6, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
            sunsetTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 20, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
            moonriseTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 8, minute: 30, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
            moonsetTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 22, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
            timeZone: timeZone,
            latitude: 27.183,
            longitude: 62.832
        )).screenPreview()
        
        HomeContent(state: HomeViewModel.StateLoading())
            .screenPreview()

        HomeContent(state: HomeViewModel.StateNoLocationSelected())
            .screenPreview()
    }
}
