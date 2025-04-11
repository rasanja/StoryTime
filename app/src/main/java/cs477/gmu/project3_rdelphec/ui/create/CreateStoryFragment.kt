package cs477.gmu.project3_rdelphec.ui.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import cs477.gmu.project3_rdelphec.databinding.FragmentCreateStoryBinding

class CreateStoryFragment : Fragment() {

    private var _binding: FragmentCreateStoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val createStoryViewModel = ViewModelProvider(this).get(CreateStoryViewModel::class.java)

        _binding = FragmentCreateStoryBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val textView: TextView = binding.csTv
        createStoryViewModel.text.observe(viewLifecycleOwner){
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}