import SwiftUI
import Shared

struct AddLocationView : View {

    @StateObject
    private var observableModel = ObservableAddLocationViewModel()
    
    @State
    var label = ""
    
    @State
    var latitude = ""
    
    @State
    var longitude = ""
    
    @State
    var timeZone = ""
    
    var body: some View {
        VStack {
            AddLocationContent(
                state: observableModel.state,
                onSubmitClick: { label, latitude, longitude, timeZone in
                    observableModel.createLocation(
                        label: label,
                        latitude: latitude,
                        longitude: longitude,
                        timeZone: timeZone
                    )
                },
                onGeocodeClick: { label in
                    observableModel.geocodeLocation(label)
                },
                onGpsClick: {
                    observableModel.useGpsLocation()
                },
                label: $label,
                latitude: $latitude,
                longitude: $longitude,
                timeZone: $timeZone
            )
        }
        .bindModel(observableModel)
        .bindBackNavigation(trigger: observableModel.goBack)
        .onReceive(observableModel.geocodeSubject, perform: { geocodeEvent in
            latitude = String(geocodeEvent.latitude)
            longitude = String(geocodeEvent.longitude)
            timeZone = geocodeEvent.timeZone
        })
    }
    
    func insertGeocodeData(latitude: Double, longitude: Double, timeZone: String) {
        self.latitude = String(latitude)
        self.longitude = String(longitude)
        self.timeZone = timeZone
    }
}

struct AddLocationContent : View {
    let state: AddLocationViewModel.State
    
    let onSubmitClick: (String, String, String, String) -> Void
    let onGeocodeClick: (String) -> Void
    let onGpsClick: () -> Void
    
    @Binding
    var label: String
    
    @Binding
    var latitude: String
    
    @Binding
    var longitude: String
    
    @Binding
    var timeZone: String

    var body: some View {
        VStack {
            TextField("Label", text: $label)
            TextField("Latitude", text: $latitude)
            TextField("Longitude", text: $longitude)
            TextField("Time Zone", text: $timeZone)
            Button("Submit") { onSubmitClick(label, latitude, longitude, timeZone) }
            Button("Geocode") { onGeocodeClick(label) } // TODO convert to bottomsheet/dialog?
            Button("Gps") { onGpsClick() }
        }
    }
}

struct AddLocationContent_Previews : PreviewProvider {
    
    @State
    static var label = "Somewhere"
    
    @State
    static var latitude = "62.831"
    
    @State
    static var longitude = "-27.182"
    
    @State
    static var timeZone = ""
    
    static var previews: some View {
        AddLocationContent(
            state: AddLocationViewModel.State(
                latitudeFormatError: false,
                longitudeFormatError: false,
                geocodeError: false,
                deviceLocationError: nil
            ),
            onSubmitClick: { _, _, _, _ in },
            onGeocodeClick: { _ in },
            onGpsClick: {},
            label: $label,
            latitude: $latitude,
            longitude: $longitude,
            timeZone: $timeZone
        ).screenPreview()
    }
}
