import SwiftUI
import Shared

struct LocationListView: View {
    @StateObject
    private var observableModel = ObservableLocationListViewModel()
    
    var body: some View {
        VStack {
            LocationListContent(
                state: observableModel.state,
                createLocationDetailLink: { id in
                    NavigationLink(
                        destination: LocationDetailView(id: id),
                        tag: id,
                        selection: $observableModel.navigateToLocationDetails,
                        label: { EmptyView() }
                    )
                },
                onAddLocationClick: { observableModel.onAddLocationClick() },
                onDeleteLocationClick: { locationId in observableModel.onRemoveLocationClick(locationId) },
                onSelectLocationClick: { locationId in observableModel.onSelectLocationClick(locationId) },
                onLocationDetailClick: { locationId in observableModel.onLocationDetailClick(locationId) }
            )
            NavigationLink(
                destination: AddLocationView(),
                isActive: $observableModel.navigateToAddLocation,
                label: { EmptyView() }
            )
        }
        .navigationBarTitleDisplayMode(.inline)
        .toolbar { ToolbarItem(placement: .principal) { Text("Locations") } }
        .bindModel(observableModel)
    }
}

struct LocationListContent : View {
    let state: LocationListViewModel.State
    
    let createLocationDetailLink: (Int64) -> NavigationLink<EmptyView, LocationDetailView>

    let onAddLocationClick: () -> Void
    let onDeleteLocationClick: (Int64) -> Void
    let onSelectLocationClick: (Int64) -> Void
    let onLocationDetailClick: (Int64) -> Void

    var body: some View {
        VStack {
            List {
                ForEach(state.locations, id: \.id) { location in
                    ZStack {
                        Button("") {} // h4x! https://stackoverflow.com/a/65932011/2565340
                        HStack {
                            Image(systemName: location.selected ? "star.fill" : "star")
                                .onTapGesture { onSelectLocationClick(location.id) }
                            Text(location.label)
                            createLocationDetailLink(location.id)
                        }.onTapGesture {
                            onLocationDetailClick(location.id)
                        }
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
