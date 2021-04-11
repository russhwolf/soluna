import SwiftUI
import Shared

struct LocationDetailView: View {
    var locationId: Int64
    
    @Environment(\.presentationMode)
    var presentationMode: Binding
    
    @Environment(\.scenePhase)
    private var scenePhase
    
    @ObservedObject
    private var observableModel: ObservableLocationDetailViewModel
        
    init(id: Int64) {
        self.locationId = id
        self.observableModel = ObservableLocationDetailViewModel(id: id)
    }
    
    var body: some View {
        VStack {
            LocationDetailContent(
                state: observableModel.state,
                onDeleteClick: { observableModel.deleteLocation() },
                onSelectLocationClick: { observableModel.toggleSelected() }
            )
        }
        .navigationTitle(observableModel.state.output is LocationDetailViewModel.StatePopulated ? (observableModel.state.output as! LocationDetailViewModel.StatePopulated).location.label : "")
        .onReceive(observableModel.$goBack, perform: { goBack in
            if goBack {
                self.presentationMode.wrappedValue.dismiss()
            }
        })
        .onChange(of: scenePhase, perform: { phase in
            if (phase != .active) {
                observableModel.reset()
            }
        })
    }
}

struct LocationDetailContent : View {
    @ObservedObject
    var state: PublishedFlow<LocationDetailViewModel.State>
    
    var onDeleteClick: () -> Void
    var onSelectLocationClick: () -> Void

    var body: some View {
        VStack {
            switch state.output {
            case is LocationDetailViewModel.StateLoading:
                Text("Loading...")
            case is LocationDetailViewModel.StateInvalidLocation:
                Text("Location is invalid!")
            case is LocationDetailViewModel.StatePopulated:
                let populatedState = state.output as! LocationDetailViewModel.StatePopulated
                Text(populatedState.location.label)
                Text("Latitude: \(populatedState.location.latitude)")
                Text("Longitude: \(populatedState.location.longitude)")
                Text("Time Zone: \(populatedState.timeZone.id)")
                Text("Sunrise: \(populatedState.sunriseTime?.toDisplayTime() ?? "None")")
                Text("Sunset: \(populatedState.sunsetTime?.toDisplayTime() ?? "None")")
                Text("Moonrise: \(populatedState.moonriseTime?.toDisplayTime() ?? "None")")
                Text("Moonset: \(populatedState.moonsetTime?.toDisplayTime() ?? "None")")
                Button("Delete") { onDeleteClick() }
                Image(systemName: populatedState.location.selected ? "star.fill" : "star")
                    .onTapGesture { onSelectLocationClick() }
            default:
                EmptyView()
            }
        }
    }
}
