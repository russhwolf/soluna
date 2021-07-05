import SwiftUI

struct ContentView: View {
    var body: some View {
        return NavigationView {
            HomeView()
        }.navigationViewStyle(StackNavigationViewStyle())
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
