import android.app.Application
import android.content.Context
import com.tomergoldst.anyface.data.DatabaseAccessPoint
import com.tomergoldst.anyface.data.Repository
import com.tomergoldst.anyface.ui.MainViewModelProvider
import com.tomergoldst.anyface.utils.AppExecutors

// Inject classes needed for various Activities and Fragments.
object InjectorUtils {

    fun getAppExecutors(): AppExecutors {
        return AppExecutors()
    }

    fun getMainViewModelProvider(application: Application): MainViewModelProvider {
        return MainViewModelProvider(application, getRepository(application))
    }

    fun getRepository(context: Context): Repository {
        val database = DatabaseAccessPoint.getDatabase(context)
        return Repository.getInstance(database.photoDao())
    }

}