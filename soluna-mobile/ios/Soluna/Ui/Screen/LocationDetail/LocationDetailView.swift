import SwiftUI
import Shared

struct LocationDetailView: View {
    
    @Environment(\.presentationMode)
    var presentationMode: Binding
    
    @StateObject
    private var observableModel: ObservableLocationDetailViewModel
        
    init(id: Int64) {
        _observableModel = StateObject(wrappedValue: ObservableLocationDetailViewModel(id: id))
    }
    
    var body: some View {
        VStack {
            LocationDetailContent(
                state: observableModel.state,
                onDeleteClick: { observableModel.deleteLocation() },
                onSelectLocationClick: { observableModel.toggleSelected() }
            )
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { ToolbarItem(placement: .principal) {
            let title = observableModel.state is LocationDetailViewModel.StatePopulated ? (observableModel.state as! LocationDetailViewModel.StatePopulated).location.label : ""
            Text(title)
        } }
        .bindModel(observableModel)
        .bindBackNavigation(trigger: observableModel.goBack)
    }
}

struct LocationDetailContent : View {
    let state: LocationDetailViewModel.State
    
    let onDeleteClick: () -> Void
    let onSelectLocationClick: () -> Void

    var body: some View {
        VStack {
            switch state {
            case is LocationDetailViewModel.StateLoading:
                Text("Loading...")
            case is LocationDetailViewModel.StateInvalidLocation:
                Text("Location is invalid!")
            case is LocationDetailViewModel.StatePopulated:
                let populatedState = state as! LocationDetailViewModel.StatePopulated
                let timeZone = TimeZone.init(identifier: populatedState.timeZone.id) ?? TimeZone.current // TODO error handling here
                Text(populatedState.location.label)
                Text("Latitude: \(populatedState.location.latitude)")
                Text("Longitude: \(populatedState.location.longitude)")
                Text("Time Zone: \(populatedState.timeZone.id)")
                Text("Sunrise: \(populatedState.sunriseTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Sunset: \(populatedState.sunsetTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Moonrise: \(populatedState.moonriseTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Text("Moonset: \(populatedState.moonsetTime?.toDisplayTime(timeZone: timeZone) ?? "None")")
                Button("Delete") { onDeleteClick() }
                Image(systemName: populatedState.location.selected ? "star.fill" : "star")
                    .onTapGesture { onSelectLocationClick() }
            default:
                EmptyView()
            }
        }
    }
}
