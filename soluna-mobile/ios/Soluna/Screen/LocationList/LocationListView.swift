import SwiftUI
import Shared

struct LocationListView: View {
    @Environment(\.scenePhase)
    private var scenePhase
    
    @ObservedObject
    private var observableModel = ObservableLocationListViewModel()
    
    var body: some View {
        VStack {
            LocationListContent(
                state: observableModel.state,
                onAddLocationClick: { observableModel.onAddLocationClick() },
                onDeleteLocationClick: { locationId in observableModel.onRemoveLocationClick(locationId) },
                onSelectLocationClick: { locationId in observableModel.onSelectLocationClick(locationId) }
            )
            NavigationLink(destination: AddLocationView(), isActive: $observableModel.navigateToAddLocation) { EmptyView() }
        }
        .navigationTitle("Locations")
        .onChange(of: scenePhase, perform: { phase in
            if (phase != .active) {
                observableModel.reset()
            }
        })
    }
}

struct LocationListContent : View {
    @ObservedObject
    var state: PublishedFlow<LocationListViewModel.State>

    var onAddLocationClick: () -> Void
    
    var onDeleteLocationClick: (Int64) -> Void
    
    var onSelectLocationClick: (Int64) -> Void

    var body: some View {
        VStack {
            List {
                ForEach(state.output.locations) { location in
                    HStack {
                        Image(systemName: location.selected ? "star.fill" : "star")
                            .onTapGesture { onSelectLocationClick(location.id) }
                        Text(location.label)
                    }
                }.onDelete { indexSet in
                    indexSet.forEach { index in
                        onDeleteLocationClick(state.output.locations[index].id)
                    }
                }
            }
            Button("Add Location") {
                onAddLocationClick()
            }
        }
    }
}

extension SelectableLocation : Identifiable {
    
}

extension SelectableLocationSummary : Identifiable {
    
}
