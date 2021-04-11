import SwiftUI
import Shared

struct LocationListView: View {
    @ObservedObject
    private var observableModel = ObservableLocationListViewModel()
    
    var body: some View {
        VStack {
            LocationListContent(
                state: $observableModel.state,
                selection: $observableModel.navigateToLocationDetails,
                onAddLocationClick: { observableModel.onAddLocationClick() },
                onDeleteLocationClick: { locationId in observableModel.onRemoveLocationClick(locationId) },
                onSelectLocationClick: { locationId in observableModel.onSelectLocationClick(locationId) },
                onLocationDetailClick: { locationId in observableModel.onLocationDetailClick(locationId) }
            )
            NavigationLink(destination: AddLocationView(), isActive: $observableModel.navigateToAddLocation) { EmptyView() }
        }
        .navigationTitle("Locations")
        .bindModel(observableModel)
    }
}

struct LocationListContent : View {
    @Binding
    var state: LocationListViewModel.State
    
    @Binding
    var selection: Int64?

    var onAddLocationClick: () -> Void
    var onDeleteLocationClick: (Int64) -> Void
    var onSelectLocationClick: (Int64) -> Void
    var onLocationDetailClick: (Int64) -> Void

    var body: some View {
        VStack {
            List {
                ForEach(state.locations) { location in
                    HStack {
                        Image(systemName: location.selected ? "star.fill" : "star")
                            .onTapGesture { onSelectLocationClick(location.id) }
                        Text(location.label)
                        NavigationLink(destination: LocationDetailView(id: location.id), tag: location.id, selection: $selection) { EmptyView() }
                    }.onTapGesture {
                        onLocationDetailClick(location.id)
                    }
                }.onDelete { indexSet in
                    indexSet.forEach { index in
                        onDeleteLocationClick(state.locations[index].id)
                    }
                }
            }
            Button("Add Location") {
                onAddLocationClick()
            }
        }
    }
}
