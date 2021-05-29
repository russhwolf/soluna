import SwiftUI
import Shared

struct HomeView: View {
    @StateObject
    private var observableModel = ObservableHomeViewModel()
        
    var body: some View {
        VStack {
            HomeContent(state: observableModel.state, onNavigateToLocationList: observableModel.onNavigateToLocationList)
            NavigationLink(destination: LocationListView(), isActive: $observableModel.navigateToLocationList) { EmptyView() }
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { ToolbarItem(placement: .principal) { Text("Soluna") } }
        .bindModel(observableModel)
    }
}

struct HomeContent : View {
    let state: HomeViewModel.State

    let onNavigateToLocationList: () -> Void

    var body: some View {
        VStack {
            switch state {
            case is HomeViewModel.StateLoading:
                Text("Loading...")
            case is HomeViewModel.StateNoLocationSelected:
                Text("No location is selected!")
            case is HomeViewModel.StatePopulated:
                let populatedState = state as! HomeViewModel.StatePopulated
                Text(populatedState.locationName)
                Text(populatedState.currentTime.toDisplayTime())
                Text(populatedState.timeZone.id)
                Text("Sunrise: \(populatedState.sunriseTime?.toDisplayTime() ?? "None")")
                Text("Sunset: \(populatedState.sunsetTime?.toDisplayTime() ?? "None")")
                Text("Moonrise: \(populatedState.moonriseTime?.toDisplayTime() ?? "None")")
                Text("Moonset: \(populatedState.moonsetTime?.toDisplayTime() ?? "None")")
            default:
                EmptyView()
            }
            Button("Locations") {
                onNavigateToLocationList()
            }
        }
    }
}
