import SwiftUI
import Shared

struct HomeView: View {
    @Environment(\.scenePhase)
    private var scenePhase
    
    @ObservedObject
    private var observableModel = ObservableHomeViewModel()
        
    var body: some View {
        VStack {
            HomeContent(state: observableModel.state, onNavigateToLocationList: observableModel.onNavigateToLocationList)
            NavigationLink(destination: LocationListView(), isActive: $observableModel.navigateToLocationList) { EmptyView() }
        }
        .navigationTitle("Soluna")
        .onChange(of: scenePhase, perform: { phase in
            if (phase != .active) {
                observableModel.reset()
            }
        })
    }
}

struct HomeContent : View {
    @ObservedObject
    var state: PublishedFlow<HomeViewModel.State>

    var onNavigateToLocationList: () -> Void

    var body: some View {
        VStack {
            switch state.output {
            case is HomeViewModel.StateLoading:
                Text("Loading...")
            case is HomeViewModel.StateNoLocationSelected:
                Text("No location is selected!")
            case is HomeViewModel.StatePopulated:
                let populatedState = state.output as! HomeViewModel.StatePopulated
                Text(populatedState.locationName)
                Text(populatedState.currentTime.toDisplayTime())
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
