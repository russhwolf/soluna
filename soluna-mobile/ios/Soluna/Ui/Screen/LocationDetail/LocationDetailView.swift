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

struct LocationDetailContent_Previews : PreviewProvider {
    private static let timeZone =
        Shared.TimeZone.Companion().of(zoneId: "America/New_York")
    
    static var previews: some View {
        LocationDetailContent(
            state: LocationDetailViewModel.StatePopulated(
                location: SelectableLocation(
                    id: 0,
                    label: "Home",
                    latitude: 27.183,
                    longitude: 62.832,
                    timeZone: timeZone.id,
                    selected: true
                ),
                currentTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 11, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
                sunriseTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 6, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
                sunsetTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 20, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
                moonriseTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 8, minute: 30, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
                moonsetTime: LocalDateTime(year: 2021, monthNumber: 1, dayOfMonth: 1, hour: 22, minute: 0, second: 0, nanosecond: 0).toInstant(timeZone: timeZone),
                timeZone: timeZone
            ),
            onDeleteClick: {},
            onSelectLocationClick: {}
        ).screenPreview()
        
        HomeContent(state: HomeViewModel.StateLoading())
            .screenPreview()

        HomeContent(state: HomeViewModel.StateNoLocationSelected())
            .screenPreview()
    }
}
